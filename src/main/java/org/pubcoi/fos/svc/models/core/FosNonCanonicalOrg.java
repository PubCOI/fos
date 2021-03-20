package org.pubcoi.fos.svc.models.core;

import org.springframework.util.DigestUtils;

import static org.pubcoi.fos.svc.services.Utils.normalise;

/**
 * Use this class to store orgs on fos_organisations that cannot be manually resolved
 * Once resolved to a proper company, this should be converted to a FosCanonicalOrg type
 * NOTE that the hash method is unsafe and so NO assumptions should be made re how many
 * other nodes refer to a single FUO instance
 */
public class FosNonCanonicalOrg extends FosOrganisation {
    FosNonCanonicalOrg() {
    }

    String companyName;
    String companyAddress;
    String referenceType;
    String reference;

    public static String resolveId(CFAward award) {
        return String.format("hash:%s", DigestUtils.md5DigestAsHex(
                (normalise(award.getOrgReference()) +
                        normalise(award.getOrgReferenceType().toString()) +
                        normalise(award.getSupplierName()) +
                        normalise(award.getSupplierAddress())
                ).getBytes()));
    }

    public FosNonCanonicalOrg(CFAward award) {
        this.id = resolveId(award);
        this.reference = award.getOrgReference();
        this.referenceType = award.getOrgReferenceType().toString();
        this.companyName = award.getSupplierName();
        this.companyAddress = award.getSupplierAddress();
    }

    public String getCompanyName() {
        return companyName;
    }

    public FosNonCanonicalOrg setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public FosNonCanonicalOrg setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return this;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public FosNonCanonicalOrg setReferenceType(String referenceType) {
        this.referenceType = referenceType;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public FosNonCanonicalOrg setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    public String toString() {
        return "FosNonCanonicalOrg{" +
                "id='" + id + '\'' +
                '}';
    }
}
