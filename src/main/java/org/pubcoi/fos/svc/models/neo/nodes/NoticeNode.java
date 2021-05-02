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
import org.neo4j.ogm.annotation.*;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.models.core.Constants;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "Notice")
@NodeEntity(label = "Notice")
public class NoticeNode implements FosEntity {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @GeneratedValue
    @org.springframework.data.neo4j.core.schema.GeneratedValue
    Long graphId;
    @Index(unique = true)
    String fosId;

    @Relationship(Constants.Neo4J.REL_AWARDED)
    @org.springframework.data.neo4j.core.schema.Relationship(Constants.Neo4J.REL_AWARDED)
    Set<AwardNode> awards;

    Boolean hidden = false;

    public NoticeNode() {
    }

    public NoticeNode(FullNotice notice) {
        this.fosId = notice.getId();
    }

    public String getFosId() {
        return fosId;
    }

    public NoticeNode setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public NoticeNode addAward(AwardNode awardNode) {
        if (null == this.awards) this.awards = new HashSet<>();
        this.awards.add(awardNode.withStartNode(this));
        return this;
    }

    public Set<AwardNode> getAwards() {
        return null == awards ? null : Collections.unmodifiableSet(awards);
    }

    public NoticeNode setAwards(Set<AwardNode> awards) {
        this.awards = awards;
        return this;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public NoticeNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NoticeNode that = (NoticeNode) o;

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

    @Override
    public String toString() {
        return "NoticeNode{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                '}';
    }
}
