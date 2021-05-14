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

package org.pubcoi.fos.svc.models.neo.nodes;

import com.opencorporates.schemas.OCCompanySchema;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.relationships.OrgLELink;
import org.pubcoi.fos.svc.models.neo.relationships.OrgPersonLink;
import org.springframework.data.neo4j.core.schema.*;

import java.util.*;

@Node(primaryLabel = "Organisation")
public class OrganisationNode implements FosEntity {

    @Id
    @GeneratedValue
    Long graphId;
    String fosId;
    String jurisdiction;
    String reference;
    String name;
    Boolean verified;
    Boolean hidden = false;

    @DynamicLabels
    Set<String> labels = new HashSet<>();

    @Relationship(Constants.Neo4J.REL_LEGAL_ENTITY)
    OrgLELink legalEntity;

    @Relationship(Constants.Neo4J.REL_PERSON)
    List<OrgPersonLink> orgPersons;

    public OrganisationNode() {
    }

    public OrganisationNode(FosOrganisation org) {
        this.fosId = org.getFosId();
        this.jurisdiction = org.getJurisdiction();
        this.reference = org.getReference();
        this.name = org.getCompanyName();
        this.verified = org.getVerified();
    }

    public OrganisationNode(OCCompanySchema ocCompany) {
        this.fosId = String.format("%s:%s", ocCompany.getJurisdictionCode(), ocCompany.getCompanyNumber());
        this.jurisdiction = ocCompany.getJurisdictionCode();
        this.reference = ocCompany.getCompanyNumber();
        this.name = ocCompany.getName();
        this.verified = true;
    }

    // used for replaying transactions
    public OrganisationNode(NodeReference target) {
        this.fosId = target.getFosId();
    }

    public String getName() {
        return name;
    }

    public OrganisationNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getFosId() {
        return fosId;
    }

    public OrganisationNode setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public OrgLELink getLegalEntity() {
        return legalEntity;
    }

    public OrganisationNode setLegalEntity(OrgLELink legalEntity) {
        this.legalEntity = legalEntity;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    OrganisationNode setLabels(Set<String> labels) {
        this.labels = labels;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public OrganisationNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public OrganisationNode setVerified(Boolean verified) {
        this.verified = verified;
        if (verified) {
            labels.add("Verified");
        } else {
            labels.remove("Verified");
        }
        return this;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public OrganisationNode setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public OrganisationNode setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationNode that = (OrganisationNode) o;

        return new EqualsBuilder()
                .append(graphId, that.graphId)
                .append(fosId, that.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .append(fosId)
                .toHashCode();
    }

    public Long getGraphId() {
        return graphId;
    }

    public OrganisationNode addPerson(OrgPersonLink orgPersonLink) {
        if (null == this.orgPersons) this.orgPersons = new ArrayList<>();
        this.orgPersons.add(orgPersonLink);
        return this;
    }

    public List<OrgPersonLink> getOrgPersons() {
        return null == orgPersons ? null : Collections.unmodifiableList(orgPersons);
    }

    public OrganisationNode setOrgPersons(List<OrgPersonLink> orgPersons) {
        this.orgPersons = orgPersons;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("graphId", graphId)
                .append("fosId", fosId)
                .append("name", name)
                .append("verified", verified)
                .toString();
    }
}
