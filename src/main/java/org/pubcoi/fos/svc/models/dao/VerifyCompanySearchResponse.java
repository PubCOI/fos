package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.oc.OCCompanySchemaWrapper;

import java.net.URI;

public class VerifyCompanySearchResponse {
    String id;
    String name;
    String address;
    URI chUrl;
    URI ocUrl;

    public VerifyCompanySearchResponse(OCCompanySchemaWrapper company) {
        if (null == company || null == company.getCompany()) return;
        this.id = company.getCompany().getId();
        this.name = company.getCompany().getName();
        this.address = company.getCompany().getRegisteredAddressInFull();
        this.chUrl = company.getCompany().getRegistryUrl();
        this.ocUrl = company.getCompany().getOpencorporatesUrl();
    }

    public String getId() {
        return id;
    }

    public VerifyCompanySearchResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VerifyCompanySearchResponse setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public VerifyCompanySearchResponse setAddress(String address) {
        this.address = address;
        return this;
    }

    public URI getChUrl() {
        return chUrl;
    }

    public VerifyCompanySearchResponse setChUrl(URI chUrl) {
        this.chUrl = chUrl;
        return this;
    }

    public URI getOcUrl() {
        return ocUrl;
    }

    public VerifyCompanySearchResponse setOcUrl(URI ocUrl) {
        this.ocUrl = ocUrl;
        return this;
    }
}
