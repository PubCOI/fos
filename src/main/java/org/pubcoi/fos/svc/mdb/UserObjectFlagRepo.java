package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.mdb.UserObjectFlag;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserObjectFlagRepo extends MongoRepository<UserObjectFlag, String> {
    boolean existsByEntityIdAndUid(String entityId, String uid);
}
