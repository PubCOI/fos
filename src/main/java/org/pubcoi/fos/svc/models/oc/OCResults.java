package org.pubcoi.fos.svc.models.oc;

import com.opencorporates.schemas.OCCompanySchema;

import java.util.List;

public class OCResults {

    // used in pulling back single companies
    OCCompanySchema company;

    // used in performing searches
    List<OCCompanySchemaWrapper> companies;

    public OCCompanySchema getCompany() {
        return company;
    }

    public OCResults setCompany(OCCompanySchema company) {
        this.company = company;
        return this;
    }

    public List<OCCompanySchemaWrapper> getCompanies() {
        return companies;
    }

    public OCResults setCompanies(List<OCCompanySchemaWrapper> companies) {
        this.companies = companies;
        return this;
    }
}
