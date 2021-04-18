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

package org.pubcoi.fos.svc.models.oc;

import com.opencorporates.schemas.OCCompanySchema;

import java.util.List;

public class OCResults {

    // used in pulling back single companies
    OCCompanySchema company;

    // used in performing searches
    List<OCCompanySchemaWrapper> companies;

    public OCCompanySchema getCompany() {
        return company;
    }

    public OCResults setCompany(OCCompanySchema company) {
        this.company = company;
        return this;
    }

    public List<OCCompanySchemaWrapper> getCompanies() {
        return companies;
    }

    public OCResults setCompanies(List<OCCompanySchemaWrapper> companies) {
        this.companies = companies;
        return this;
    }
}
