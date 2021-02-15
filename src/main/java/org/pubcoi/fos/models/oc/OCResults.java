package org.pubcoi.fos.models.oc;

import org.pubcoi.fos.models.mdb.OCCompany;

public class OCResults {

    OCCompany company;

    public OCCompany getCompany() {
        return company;
    }

    public OCResults setCompany(OCCompany company) {
        this.company = company;
        return this;
    }
}
