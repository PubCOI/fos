package org.pubcoi.fos.svc.mdb;

import org.pubcoi.cdm.mnis.MnisMemberType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MnisMembersRepo extends MongoRepository<MnisMemberType, Integer> {
}
