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

package org.pubcoi.fos.svc.models.neo.relationships;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.ArrayList;
import java.util.List;

@RelationshipProperties
public class OrgLELink implements FosRelationship {

    @Id @GeneratedValue
    Long graphId;

    String fosId;

    @TargetNode
    OrganisationNode organisation;

    List<String> transactions = new ArrayList<>();

    Boolean hidden = false;

    public OrgLELink() {}

    public OrgLELink(OrganisationNode source, OrganisationNode target, String transactionId)  {
        this.fosId = DigestUtils.sha1Hex(String.format("%s:%s", source.getFosId(), target.getFosId()));
        this.organisation = target;
        this.transactions.add(transactionId);
    }

    public Boolean getHidden() {
        return hidden;
    }

    public OrgLELink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getFosId() {
        return fosId;
    }

    public OrgLELink setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public OrganisationNode getOrganisation() {
        return organisation;
    }

    public OrgLELink setOrganisation(OrganisationNode organisation) {
        this.organisation = organisation;
        return this;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public OrgLELink setTransactions(List<String> transactions) {
        this.transactions = transactions;
        return this;
    }

    @Override
    public Long getGraphId() {
        return graphId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrgLELink orgLELink = (OrgLELink) o;

        return new EqualsBuilder()
                .append(graphId, orgLELink.graphId)
                .append(fosId, orgLELink.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .append(fosId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "OrgLELink{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                '}';
    }
}
