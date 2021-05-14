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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDate;

@RelationshipProperties
public class AwardOrgLink implements FosRelationship {

    @Id
    @GeneratedValue
    Long graphId;

    @TargetNode
    OrganisationNode organisationNode;

    LocalDate awardedDate;
    LocalDate startDate;
    LocalDate endDate;

    Boolean hidden = false;

    AwardOrgLink() {}

    public AwardOrgLink(OrganisationNode organisationNode, LocalDate awardedDate, LocalDate startDate, LocalDate endDate){
        this.organisationNode = organisationNode;
        this.awardedDate = awardedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public OrganisationNode getOrganisationNode() {
        return organisationNode;
    }

    public AwardOrgLink setOrganisationNode(OrganisationNode organisationNode) {
        this.organisationNode = organisationNode;
        return this;
    }

    public LocalDate getAwardedDate() {
        return awardedDate;
    }

    public AwardOrgLink setAwardedDate(LocalDate awardedDate) {
        this.awardedDate = awardedDate;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public AwardOrgLink setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public AwardOrgLink setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public AwardOrgLink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public Long getGraphId() {
        return graphId;
    }

    @Override
    public String toString() {
        return "AwardOrgLink{" +
                "graphId=" + graphId +
                ", organisationNode=" + organisationNode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AwardOrgLink that = (AwardOrgLink) o;

        return new EqualsBuilder()
                .append(graphId, that.graphId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .toHashCode();
    }

}
