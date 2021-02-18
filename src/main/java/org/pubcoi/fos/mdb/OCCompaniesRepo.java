package org.pubcoi.fos.mdb;

import com.opencorporates.schemas.OCCompanySchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OCCompaniesRepo extends MongoRepository<OCCompanySchema, String> {

    boolean existsByCompanyNumber(String companyNumber);

}
