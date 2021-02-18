package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.DataSources;
import org.pubcoi.fos.models.core.FOSCompany;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FOSCompaniesRepo extends MongoRepository<FOSCompany, String> {
    boolean existsBySourceAndReference(DataSources DataSources, String reference);
}
