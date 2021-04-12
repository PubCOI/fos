package org.pubcoi.fos.svc.models.oc;

import com.opencorporates.schemas.OCCompanySchema;

public class OCCompanySchemaWrapper {
    OCCompanySchema company;

    public OCCompanySchema getCompany() {
        return company;
    }

    public OCCompanySchemaWrapper setCompany(OCCompanySchema company) {
        this.company = company;
        return this;
    }
}
