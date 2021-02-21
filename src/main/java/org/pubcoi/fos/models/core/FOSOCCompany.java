package org.pubcoi.fos.models.core;

public class FOSOCCompany extends FOSOrganisation {

    private static final String PREFIX = "oc_company";

    String jurisdiction;
    String reference;
    String license;

    public FOSOCCompany() {}

    public FOSOCCompany(String jurisdiction, String orgReference) {
        super();
        this.id = String.format("%s:%s:%s", PREFIX, jurisdiction, orgReference);
        this.jurisdiction = jurisdiction;
        this.reference = orgReference;
        this.license = "OKF ODbL 1.0 via OpenCorporates.com";
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public String getReference() {
        return reference;
    }

    public String getLicense() {
        return license;
    }
}
