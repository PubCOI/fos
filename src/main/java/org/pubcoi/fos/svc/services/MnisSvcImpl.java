package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.mnis.MnisMembersType;
import org.pubcoi.fos.svc.repos.mdb.MnisInterestsCacheRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisMembersRepo;
import org.pubcoi.fos.svc.models.core.MnisInterestsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Service
public class MnisSvcImpl implements MnisSvc {
    private static final Logger logger = LoggerFactory.getLogger(MnisSvcImpl.class);

    final MnisMembersRepo mnisMembersRepo;
    final MnisInterestsCacheRepo mnisInterestsCacheRepo;
    final RestTemplate restTemplate;

    @Value("${fos.endpoint.mnis-interests:http://data.parliament.uk/membersdataplatform/services/mnis/members/query/Id=%s/Interests/}")
    String interestsEndpoint;

    public MnisSvcImpl(MnisMembersRepo mnisMembersRepo,
                       MnisInterestsCacheRepo mnisInterestsCacheRepo,
                       RestTemplate restTemplate) {
        this.mnisMembersRepo = mnisMembersRepo;
        this.mnisInterestsCacheRepo = mnisInterestsCacheRepo;
        this.restTemplate = restTemplate;
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
                if (null == membersResponse.getBody().getMember() || membersResponse.getBody().getMember().size() != 1) {
                    logger.error("Did not get expected response");
                    return;
                }
                MnisMemberType member = membersResponse.getBody().getMember().get(0);
                mnisInterestsCacheRepo.save(new MnisInterestsCache(member));
                mnisMembersRepo.save(mnisMembersRepo.findById(memberId).orElseThrow().withInterests(member.getInterests()));
            }
        }
    }

}
