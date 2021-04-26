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

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;

@RelationshipProperties
public class AwardOrgLink implements FosRelationship {

    @Id @GeneratedValue
    Long graphId;

    @TargetNode
    OrganisationNode organisationNode;

    ZonedDateTime awardedDate;
    ZonedDateTime startDate;
    ZonedDateTime endDate;

    Boolean hidden = false;

    AwardOrgLink() {}

    public AwardOrgLink(OrganisationNode organisationNode, ZonedDateTime awardedDate, ZonedDateTime startDate, ZonedDateTime endDate){
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

    public ZonedDateTime getAwardedDate() {
        return awardedDate;
    }

    public AwardOrgLink setAwardedDate(ZonedDateTime awardedDate) {
        this.awardedDate = awardedDate;
        return this;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public AwardOrgLink setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public AwardOrgLink setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
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
}
