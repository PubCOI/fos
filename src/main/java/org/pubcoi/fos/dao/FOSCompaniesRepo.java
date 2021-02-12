package org.pubcoi.fos.dao;

import org.pubcoi.fos.models.ch.FOSCompany;
import org.pubcoi.fos.models.ch.FOSReferenceTypeE;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FOSCompaniesRepo extends MongoRepository<FOSCompany, String> {
    boolean existsByReferenceTypeAndReference(FOSReferenceTypeE FOSReferenceTypeE, String reference);
}
