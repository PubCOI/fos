package org.pubcoi.fos.svc.models.core;

public class FosOCCompany extends FosOrganisation {

    private static final String PREFIX = "oc_company";

    String jurisdiction;
    String reference;
    String license;

    public FosOCCompany() {}

    public FosOCCompany(String jurisdiction, String orgReference) {
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
