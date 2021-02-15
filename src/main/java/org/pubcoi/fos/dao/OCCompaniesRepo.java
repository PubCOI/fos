package org.pubcoi.fos.dao;

import org.pubcoi.fos.models.mdb.OCCompany;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OCCompaniesRepo extends MongoRepository<OCCompany, String> {

    boolean existsByCompanyNumber(String companyNumber);

}
