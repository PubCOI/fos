package org.pubcoi.fos.dao;

import com.opencorporates.schemas.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OCCompaniesRepo extends MongoRepository<Company, String> {

    boolean existsByCompanyNumber(String companyNumber);

}
