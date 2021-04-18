/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.oc.OCCompanySchemaWrapper;

import java.net.URI;

public class VerifyCompanySearchResponse {
    String id;
    String name;
    String address;
    URI chUrl;
    URI ocUrl;
    Boolean flagged = false;

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

    public Boolean getFlagged() {
        return flagged;
    }

    public VerifyCompanySearchResponse setFlagged(Boolean flagged) {
        this.flagged = flagged;
        return this;
    }
}
