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

package org.pubcoi.fos.svc.models.core;

import com.opencorporates.schemas.OCCompanySchema;

/**
 * A canonical org is one we've added and we're 99.99% sure that it's the same as the reference
 * given in Companies House etc.
 */
public class FosCanonicalOrg extends FosOrganisation {

    String license;
    String ocRefURL;

    FosCanonicalOrg() {}

    public FosCanonicalOrg(OCCompanySchema ocCompany) {
        super();
        this.fosId = String.format("%s:%s", ocCompany.getJurisdictionCode(), ocCompany.getCompanyNumber());
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
