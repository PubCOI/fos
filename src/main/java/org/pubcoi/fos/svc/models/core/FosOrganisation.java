package org.pubcoi.fos.svc.models.core;

import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fos_organisations")
public abstract class FosOrganisation implements FosEntity {
    @Id
    String id;
    String jurisdiction;
    String companyName;
    String companyAddress;
    String referenceType;
    String reference;
    Boolean hidden = false;
    Boolean verified = false;

    public String getId() {
        return id;
    }

    FosOrganisation() {
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public FosOrganisation setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public FosOrganisation setId(String id) {
        this.id = id;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public FosOrganisation setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public FosOrganisation setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public FosOrganisation setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public FosOrganisation setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public FosOrganisation setReferenceType(String referenceType) {
        this.referenceType = referenceType;
        return this;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public FosOrganisation setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
        return this;
    }
}
