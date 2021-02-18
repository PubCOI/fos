package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.Award;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AwardsMDBRepo extends MongoRepository<Award, String> {
}
