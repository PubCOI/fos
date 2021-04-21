/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.pubcoi.cdm.mnis.MnisInterestCategoryType;
import org.pubcoi.cdm.mnis.MnisInterestType;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.mnis.MnisMembersType;
import org.pubcoi.cdm.pw.RegisterCategoryType;
import org.pubcoi.cdm.pw.RegisterEntryType;
import org.pubcoi.cdm.pw.RegisterRecordType;
import org.pubcoi.fos.svc.exceptions.FosCoreException;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.pubcoi.fos.svc.models.core.MnisInterestsCache;
import org.pubcoi.fos.svc.models.es.*;
import org.pubcoi.fos.svc.repos.es.MembersInterestsESRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisInterestsCacheRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisMembersRepo;
import org.pubcoi.fos.svc.repos.mdb.ParentRecordTypeMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.PWDeclaredInterestMDBRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MnisSvcImpl implements MnisSvc {
    private static final Logger logger = LoggerFactory.getLogger(MnisSvcImpl.class);

    final MnisMembersRepo mnisMembersRepo;
    final MnisInterestsCacheRepo mnisInterestsCacheRepo;
    final MembersInterestsESRepo membersInterestsESRepo;
    final PWDeclaredInterestMDBRepo pwDeclaredInterestMDBRepo;
    final RestTemplate restTemplate;
    final PWDeclaredInterestFactory declaredInterestFactory;
    final ParentRecordTypeMDBRepo parentRecordTypeMDBRepo;
    Map<String, Integer> pwToMemberLookup = new HashMap<>();
    Map<Integer, String> memberToPwLookup = new HashMap<>();

    @Value("${fos.endpoint.mnis-interests:http://data.parliament.uk/membersdataplatform/services/mnis/members/query/Id=%s/Interests/}")
    String interestsEndpoint;

    @Value("${fos.xsl.pw.mnis-input:mnisid_to_pwid.json}")
    String mnisMapInput;

    @PostConstruct
    public void populateMap() {
        // we don't need any models in this case
        // see https://gist.github.com/rmacd/7ee7c5a781f50e5984f9bad2a2dda0ff
        ClassPathResource mapInputResource = new ClassPathResource(mnisMapInput);
        if (!mapInputResource.exists()) {
            throw new FosRuntimeException(String.format("Unable to find file at path %s", mnisMapInput));
        }
        try (InputStream is = mapInputResource.getInputStream()) {
            JSONArray arr = new JSONArray(new String(is.readAllBytes()));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = (JSONObject) arr.get(i);
                pwToMemberLookup.put(o.getString("pw_id"), o.getInt("member_id"));
                memberToPwLookup.put(o.getInt("member_id"), o.getString("pw_id"));
            }
            // todo this should probably happen some place else ... as it stands bootstraping currently
            // requires restart
            for (Map.Entry<Integer, String> integerStringEntry : memberToPwLookup.entrySet()) {
                mnisMembersRepo.findById(integerStringEntry.getKey())
                        .ifPresent(m -> {
                            if (null == m.getPwId()) {
                                logger.debug("pwId for {} is null: populating entry", m.getMemberId());
                                String pwId = memberToPwLookup.get(m.getMemberId());
                                if (null == pwId) {
                                    logger.error("pwId for {} cannot be found", m.getMemberId());
                                } else {
                                    mnisMembersRepo.save(m.setPwId(pwId));
                                }
                            }
                        });
            }
        } catch (IOException | JSONException e) {
            throw new FosRuntimeException("Unable to construct member ID lookup map");
        }
    }

    public MnisSvcImpl(MnisMembersRepo mnisMembersRepo,
                       MnisInterestsCacheRepo mnisInterestsCacheRepo,
                       MembersInterestsESRepo membersInterestsESRepo,
                       PWDeclaredInterestMDBRepo pwDeclaredInterestMDBRepo,
                       RestTemplate restTemplate,
                       PWDeclaredInterestFactory declaredInterestFactory,
                       ParentRecordTypeMDBRepo parentRecordTypeMDBRepo) {
        this.mnisMembersRepo = mnisMembersRepo;
        this.mnisInterestsCacheRepo = mnisInterestsCacheRepo;
        this.membersInterestsESRepo = membersInterestsESRepo;
        this.pwDeclaredInterestMDBRepo = pwDeclaredInterestMDBRepo;
        this.restTemplate = restTemplate;
        this.declaredInterestFactory = declaredInterestFactory;
        this.parentRecordTypeMDBRepo = parentRecordTypeMDBRepo;
    }

    @Override
    public void populateInterestsForMember(Integer memberId) {
        MnisInterestsCache cacheObject = mnisInterestsCacheRepo.findByMemberIdAndLastUpdatedAfter(memberId, OffsetDateTime.now().minus(3, ChronoUnit.MONTHS));
        if (null == cacheObject) {
            // populate it
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<MnisMembersType> membersResponse = restTemplate.exchange(String.format(interestsEndpoint, memberId), HttpMethod.GET, entity, MnisMembersType.class);
            if (null != membersResponse.getBody()) {
                if (null == membersResponse.getBody().getMembers() || membersResponse.getBody().getMembers().size() != 1) {
                    logger.error("Did not get expected response");
                    return;
                }
                MnisMemberType member = membersResponse.getBody().getMembers().get(0);
                mnisInterestsCacheRepo.save(new MnisInterestsCache(member));
                mnisMembersRepo.save(mnisMembersRepo.findById(memberId).orElseThrow().withInterests(member.getInterests()));
            }
        }
    }

    @Override
    public void addInterestsToMDB(RegisterEntryType entryType, String datasetName) throws FosCoreException {
        Integer memberId = pwToMemberLookup.get(entryType.getPersonId());
        MnisMemberType member;
        Set<String> flags = new HashSet<>();

        if (null == memberId) {
            logger.warn("Unable to find member ID in pw lookup, having to search by name");
            flags.add("WARN_PWID_NOT_FOUND");
            member = mnisMembersRepo.findMnisMemberTypeByFullTitleContaining(entryType.getMemberName());
            if (null == member) {
                logger.error(String.format("Unable to find member ID %s (%s)", entryType.getPersonId(), entryType.getMemberName()));
                return;
            }
        } else {
            Optional<MnisMemberType> mdbMember = mnisMembersRepo.findById(memberId);
            if (mdbMember.isEmpty()) {
                logger.error("Unable to find member {} ({}) in MDB", memberId, entryType.getMemberName());
                return;
            }
            member = mdbMember.get();
        }

        logger.debug("Found user {}: {}", entryType.getPersonId(), member.getFullTitle());

        for (RegisterCategoryType category : entryType.getCategories()) {
            for (RegisterRecordType record : category.getRecords()) {
                String parentRecordId = hashOf(member, entryType, record);
                record.setId(parentRecordId);
                parentRecordTypeMDBRepo.save(record);
                for (RegisterRecordType.RegisterRecordItem item : record.getItems()) {
                    if (item.getValue().equalsIgnoreCase("nil")) {
                        logger.info("Entry for {} ({}) is nil, skipping", entryType.getPersonId(), member.getFullTitle());
                    } else {
                        PWDeclaredInterest declaredInterest = declaredInterestFactory.createMDB(member, category, item, datasetName);
                        declaredInterest.getFlags().addAll(flags);
                        declaredInterest.setParentRecordId(parentRecordId);
                        upsertInterestIntoMDB(declaredInterest);
//                        addToES(member, category, item, datasetName, flags);
                    }
                }
            }
        }
    }

    /**
     * Generates a hash of the record
     *
     * @param member    member so as to include ID in hash
     * @param entryType entrytype so as to include dt
     * @return a sha1 hash that can be used to refer back to the record
     */
    private String hashOf(MnisMemberType member, RegisterEntryType entryType, RegisterRecordType recordType) {
        StringBuilder sb = new StringBuilder();

        for (RegisterRecordType.RegisterRecordItem item : recordType.getItems()) {
            sb.append(item.getValue().toUpperCase().replaceAll("[^A-Z0-9]", ""));
        }

        return DigestUtils.sha1Hex(String.format(
                "%s:%s:%s", member.getMemberId(), entryType.getDate(), sb.toString()
        ).getBytes());
    }

    @Override
    public void reindex() {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger insertions = new AtomicInteger(0);
        float totalDocs = pwDeclaredInterestMDBRepo.findAll().size() + mnisMembersRepo.findAll().size();
        pwDeclaredInterestMDBRepo.findAll().forEach(interest -> {
            logProgress(count, totalDocs);
            MnisMemberType pwMember = mnisMembersRepo.findById(interest.getMnisPersonId()).orElseThrow();
            membersInterestsESRepo.save(new MemberInterest(pwMember, interest));
            logInsertions(insertions);
        });
        mnisMembersRepo.findAll().forEach(member -> {
            logProgress(count, totalDocs);
            for (MnisInterestCategoryType category : member.getInterests().getCategories()) {
                for (MnisInterestType interest : category.getInterests()) {
                    membersInterestsESRepo.save(new MemberInterest(member, category, interest));
                    logInsertions(insertions);
                }
            }
        });
        logger.debug("Finished reindexing {} documents", count.get());
        logger.debug("Upserted {} documents in total", insertions.get());
    }

    private void logProgress(AtomicInteger count, float totalDocs) {
        int current = count.getAndIncrement();
        if (current % 100 == 0) {
            logger.debug("Reindexed {} documents ({}%)", current, String.format("%.2f", (current == 0) ? 0 : (current / totalDocs) * 100.0));
        }
    }

    private void logInsertions(AtomicInteger count) {
        int current = count.incrementAndGet();
        if (current % 100 == 0) {
            logger.debug("Upserted {} documents", current);
        }
    }

    @Override
    public void reanalyse() {
        AtomicInteger count = new AtomicInteger(0);
        float totalDocs = pwDeclaredInterestMDBRepo.findAll().size();
        pwDeclaredInterestMDBRepo.findAll().forEach(interest -> {
            int current = count.getAndIncrement();
            if (current % 100 == 0) {
                logger.debug("Reanalysed {} documents ({}%)", current, String.format("%.2f", (current == 0) ? 0 : (current / totalDocs) * 100.0));
            }
            pwDeclaredInterestMDBRepo.save((PWDeclaredInterestMDBType) interest.analyseText());
        });
        logger.debug("Finished reanalysing {} documents", count.get());
    }

    // we're adding to MDB first as it's quicker than indexing on ES (particularly at the point of waiting to commit)
    // for reference, ~10k records causes ~200k updates (where the same interest appears multiple times across
    // different datasets)
    // ... if we weren't trying to merge, we could skip this and go straight to indexing on ES
    private void upsertInterestIntoMDB(PWDeclaredInterest newInterest) {
        Optional<PWDeclaredInterestMDBType> oldInterest = pwDeclaredInterestMDBRepo.findById(newInterest.getId());
        // we want to merge flags and datasets
        if (oldInterest.isPresent()) {
            newInterest.getFlags().addAll(oldInterest.get().getFlags());
            newInterest.getDatasets().addAll(oldInterest.get().getDatasets());
        }
        pwDeclaredInterestMDBRepo.save((PWDeclaredInterestMDBType) newInterest);
    }

}
