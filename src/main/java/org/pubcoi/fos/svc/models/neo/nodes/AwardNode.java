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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.neo.relationships.AwardOrgLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.ZonedDateTime;

@Node(primaryLabel = "Award")
public class AwardNode implements FosEntity {
    private static final Logger logger = LoggerFactory.getLogger(AwardNode.class);

    @Relationship("AWARDED_TO")
    AwardOrgLink organisation;

    @Id
    String id;
    Long value;
    String noticeId;

    Boolean hidden = false;

    public AwardNode() {}

    public OrganisationNode getOrganisation() {
        return (null != organisation) ? organisation.getOrganisationNode() : null;
    }

    public AwardNode setOrganisation(OrganisationNode organisationNode, ZonedDateTime awardedDate, ZonedDateTime startDate, ZonedDateTime endDate) {
        if (null == this.organisation) {
            this.organisation = new AwardOrgLink(organisationNode).setAwardedDate(awardedDate).setStartDate(startDate).setEndDate(endDate);
        } else {
            logger.warn("REMOVING relationship between entities {} {}", this, organisationNode);
            this.organisation.setOrganisationNode(organisationNode);
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public AwardNode setId(String id) {
        this.id = id;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardNode setValue(Long value) {
        this.value = value;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AwardNode setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    @Override
    public String toString() {
        return "AwardNode{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", noticeId='" + noticeId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AwardNode awardNode = (AwardNode) o;

        return new EqualsBuilder()
                .append(id, awardNode.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public Boolean getHidden() {
        return hidden;
    }

    public AwardNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
