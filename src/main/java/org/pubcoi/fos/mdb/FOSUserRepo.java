package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.FOSUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FOSUserRepo extends MongoRepository<FOSUser, String> {
    boolean existsByUid(String uid);
    FOSUser getByUid(String uid);
}
