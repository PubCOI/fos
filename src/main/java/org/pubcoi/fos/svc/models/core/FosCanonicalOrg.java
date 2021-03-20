package org.pubcoi.fos.svc.models.core;

/**
 * A canonical org is one we've added and we're 99.99% sure that it's the same as the reference
 * given in Companies House etc.
 *
 */
public class FosCanonicalOrg extends FosOrganisation {

    String jurisdiction;
    String reference;
    String license;

    public FosCanonicalOrg() {}

    public FosCanonicalOrg(String jurisdiction, String orgReference) {
        super();
        this.id = String.format("%s:%s", jurisdiction, orgReference);
        this.jurisdiction = jurisdiction;
        this.reference = orgReference;
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
