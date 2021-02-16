package org.pubcoi.fos.models.oc;

import com.opencorporates.schemas.OCCompanySchema;

public class OCResults {

    OCCompanySchema company;

    public OCCompanySchema getCompany() {
        return company;
    }

    public OCResults setCompany(OCCompanySchema company) {
        this.company = company;
        return this;
    }
}
