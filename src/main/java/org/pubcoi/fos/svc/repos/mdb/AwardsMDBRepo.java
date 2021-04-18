package org.pubcoi.fos.svc.repos.mdb;

import org.pubcoi.fos.svc.models.core.CFAward;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface AwardsMDBRepo extends MongoRepository<CFAward, String> {
    Set<CFAward> findAllByNoticeId(String noticeID);
}
