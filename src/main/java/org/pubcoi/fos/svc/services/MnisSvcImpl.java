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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.mnis.MnisMembersType;
import org.pubcoi.cdm.pw.RegisterCategoryType;
import org.pubcoi.cdm.pw.RegisterEntryType;
import org.pubcoi.cdm.pw.RegisterRecordType;
import org.pubcoi.fos.svc.exceptions.FosCoreException;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.pubcoi.fos.svc.models.core.MnisInterestsCache;
import org.pubcoi.fos.svc.models.es.PersonDeclaredInterest;
import org.pubcoi.fos.svc.models.es.PersonDeclaredInterestFactory;
import org.pubcoi.fos.svc.repos.es.PersonDeclaredInterestESRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisInterestsCacheRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisMembersRepo;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MnisSvcImpl implements MnisSvc {
    private static final Logger logger = LoggerFactory.getLogger(MnisSvcImpl.class);

    final MnisMembersRepo mnisMembersRepo;
    final MnisInterestsCacheRepo mnisInterestsCacheRepo;
    final PersonDeclaredInterestESRepo declaredInterestESRepo;
    final RestTemplate restTemplate;
    final PersonDeclaredInterestFactory declaredInterestFactory;
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
                       PersonDeclaredInterestESRepo declaredInterestESRepo,
                       RestTemplate restTemplate,
                       PersonDeclaredInterestFactory declaredInterestFactory) {
        this.mnisMembersRepo = mnisMembersRepo;
        this.mnisInterestsCacheRepo = mnisInterestsCacheRepo;
        this.declaredInterestESRepo = declaredInterestESRepo;
        this.restTemplate = restTemplate;
        this.declaredInterestFactory = declaredInterestFactory;
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
    public void addInterestsToES(RegisterEntryType entryType, String datasetName) throws FosCoreException {
        Integer memberId = pwToMemberLookup.get(entryType.getPersonId());
        MnisMemberType member;
        boolean searchWarning = false;
        if (null == memberId) {
            logger.warn("Unable to find member ID in pw lookup, having to search by name");
            searchWarning = true;
            member = mnisMembersRepo.findMnisMemberTypeByFullTitleContaining(entryType.getMemberName());
            if (null == member) {
                logger.error(String.format("Unable to find member ID %s (%s)", entryType.getPersonId(), entryType.getMemberName()));
                return;
            }
        }
        else {
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
                for (RegisterRecordType.RegisterRecordItem item : record.getItems()) {
                    if (item.getValue().equalsIgnoreCase("nil")) {
                        logger.info("Entry for {} ({}) is nil, skipping", entryType.getPersonId(), member.getFullTitle());
                    } else {
                        PersonDeclaredInterest declaredInterest = declaredInterestFactory.create(member, category, item, datasetName);
                        if (searchWarning) {
                            declaredInterest.getFlags().add("WARN_PWID_NOT_FOUND");
                        }
                        declaredInterestESRepo.save(declaredInterest);
                        logger.debug("Created interest {}", declaredInterest.getId());
                    }
                }
            }
        }
    }

}
