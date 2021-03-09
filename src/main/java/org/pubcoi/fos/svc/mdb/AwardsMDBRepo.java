package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.CFAward;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AwardsMDBRepo extends MongoRepository<CFAward, String> {
}
