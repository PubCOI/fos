package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FosUserRepo extends MongoRepository<FosUser, String> {
    boolean existsByUid(String uid);
    FosUser getByUid(String uid);
}
