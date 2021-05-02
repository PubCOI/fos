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
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.neo.relationships.AwardOrgLink;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node(primaryLabel = "Award")
public class AwardNode implements FosEntity {

    @Relationship(Constants.Neo4J.REL_AWARDED_TO)
    List<AwardOrgLink> awardees;

    @Id
    @GeneratedValue
    Long graphId;
    String fosId;
    Long value;
    String noticeId;
    Boolean groupAward;
    Boolean hidden = false;

    public AwardNode() {
    }

    public String getFosId() {
        return fosId;
    }

    public AwardNode setFosId(String fosId) {
        this.fosId = fosId;
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

    public Boolean getHidden() {
        return hidden;
    }

    public AwardNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Boolean getGroupAward() {
        return groupAward;
    }

    public AwardNode setGroupAward(Boolean groupAward) {
        this.groupAward = groupAward;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AwardNode awardNode = (AwardNode) o;

        return new EqualsBuilder()
                .append(graphId, awardNode.graphId)
                .append(fosId, awardNode.fosId)
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
        return "AwardNode{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                ", noticeId='" + noticeId + '\'' +
                '}';
    }

    public List<AwardOrgLink> getAwardees() {
        return null == awardees ? null : Collections.unmodifiableList(awardees);
    }

    public AwardNode setAwardees(List<AwardOrgLink> awardees) {
        this.awardees = awardees;
        return this;
    }

    public AwardNode addAwardee(AwardOrgLink awardOrgLink) {
        if (null == this.awardees) awardees = new ArrayList<>();
        this.awardees.add(awardOrgLink);
        return this;
    }
}
