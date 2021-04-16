package org.pubcoi.fos.svc.mdb;


import com.opencorporates.schemas.OCCompanySchema;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public interface OCCompaniesRepo extends MongoRepository<OCCompanySchema, String> {

    boolean existsByCompanyNumber(String companyNumber);

    OCCompanySchema findByCompanyNumberAndJurisdictionCode(
            @Size(min = 1) @NotNull String companyNumber,
            @Size(min = 2, max = 5) @NotNull String jurisdictionCode
    );
}
