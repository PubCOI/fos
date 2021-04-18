package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.MnisInterestsCache;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.OffsetDateTime;

public interface MnisInterestsCacheRepo extends MongoRepository<MnisInterestsCache, Integer> {
    MnisInterestsCache findByMemberIdAndLastUpdatedAfter(Integer memberId, OffsetDateTime lastUpdated);
}
