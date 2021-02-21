package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.CFAward;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AwardsMDBRepo extends MongoRepository<CFAward, String> {
}
