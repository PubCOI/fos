package org.pubcoi.fos.models.core;

public class FOSOCCompany extends FOSOrganisation {

    private static final String PREFIX = "oc_company";

    String jurisdiction;
    String reference;

    public FOSOCCompany() {}

    public FOSOCCompany(String jurisdiction, String orgReference) {
        super();
        this.id = String.format("%s:%s:%s", PREFIX, jurisdiction, orgReference);
        this.jurisdiction = jurisdiction;
        this.reference = orgReference;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public String getReference() {
        return reference;
    }
}
