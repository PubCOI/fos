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
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;

@RelationshipProperties
public class OrgPersonLink implements FosRelationship {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @GeneratedValue
    @org.springframework.data.neo4j.core.schema.GeneratedValue
    Long graphId;

    String fosId;

    @TargetNode
    PersonNode person;
    String position;
    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    OrgPersonLink() {}

    public OrgPersonLink(PersonNode person, String organisationId, String position, ZonedDateTime startDT, ZonedDateTime endDT, String transactionId) {
        this.fosId = DigestUtils.sha1Hex(String.format("%s_%s_%s", organisationId, person.getFosId(), position));
        this.person = person;
        this.position = position;
        this.startDT = startDT;
        this.endDT = endDT;
        this.transactions.add(transactionId);
    }

    public PersonNode getPerson() {
        return person;
    }

    public OrgPersonLink setPerson(PersonNode person) {
        this.person = person;
        return this;
    }

    public ZonedDateTime getStartDT() {
        return startDT;
    }

    public OrgPersonLink setStartDT(ZonedDateTime startDT) {
        this.startDT = startDT;
        return this;
    }

    public ZonedDateTime getEndDT() {
        return endDT;
    }

    public OrgPersonLink setEndDT(ZonedDateTime endDT) {
        this.endDT = endDT;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public OrgPersonLink setPosition(String position) {
        this.position = position;
        return this;
    }

    public String getFosId() {
        return fosId;
    }

    public OrgPersonLink setFosId(String fosId) {
        this.fosId = fosId;
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

        OrgPersonLink that = (OrgPersonLink) o;

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
        return "OrgPersonLink{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                '}';
    }
}
