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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fos_organisations")
public abstract class FosOrganisation implements FosEntity {

    @Id
    String fosId;
    String jurisdiction;
    String companyName;
    String companyAddress;
    String referenceType;
    String reference;
    boolean hidden = false;
    boolean verified = false;

    public String getFosId() {
        return fosId;
    }

    FosOrganisation() {
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public FosOrganisation setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public FosOrganisation setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public boolean getVerified() {
        return verified;
    }

    public FosOrganisation setVerified(boolean verified) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fosId", fosId)
                .append("companyName", companyName)
                .append("referenceType", referenceType)
                .append("reference", reference)
                .append("verified", verified)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FosOrganisation that = (FosOrganisation) o;

        return new EqualsBuilder()
                .append(fosId, that.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fosId)
                .toHashCode();
    }
}
