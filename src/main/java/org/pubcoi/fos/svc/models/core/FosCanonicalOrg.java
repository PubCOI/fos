package org.pubcoi.fos.svc.models.core;

import com.opencorporates.schemas.OCCompanySchema;

/**
 * A canonical org is one we've added and we're 99.99% sure that it's the same as the reference
 * given in Companies House etc.
 *
 */
public class FosCanonicalOrg extends FosOrganisation {

    String license;
    String ocRefURL;

    FosCanonicalOrg() {}

    public FosCanonicalOrg(OCCompanySchema ocCompany) {
        super();
        this.id = String.format("%s:%s", ocCompany.getJurisdictionCode(), ocCompany.getCompanyNumber());
        this.companyName = ocCompany.getName();
        this.companyAddress = ocCompany.getRegisteredAddressInFull();
        this.jurisdiction = ocCompany.getJurisdictionCode();
        this.reference = ocCompany.getCompanyNumber();
        this.ocRefURL = null != ocCompany.getOpencorporatesUrl() ? ocCompany.getOpencorporatesUrl().toString() : null;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public String getLicense() {
        return license;
    }

    public FosCanonicalOrg setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
        return this;
    }

    public FosCanonicalOrg setLicense(String license) {
        this.license = license;
        return this;
    }

    public String getOcRefURL() {
        return ocRefURL;
    }

    public FosCanonicalOrg setOcRefURL(String ocRefURL) {
        this.ocRefURL = ocRefURL;
        return this;
    }
}
