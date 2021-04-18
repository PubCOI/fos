package org.pubcoi.fos.svc.repos.mdb;


import com.opencorporates.schemas.OCCompanySchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OCCompaniesRepo extends MongoRepository<OCCompanySchema, String> {

    boolean existsByCompanyNumber(String companyNumber);

    OCCompanySchema findByCompanyNumberAndJurisdictionCode(String companyNumber, String jurisdictionCode);
}
