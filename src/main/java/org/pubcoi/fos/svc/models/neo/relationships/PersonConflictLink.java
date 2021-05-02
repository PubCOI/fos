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
import org.neo4j.ogm.annotation.*;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.neo.nodes.DeclaredInterest;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;

import static org.pubcoi.fos.svc.services.Utils.mnisIdHash;

@RelationshipEntity(Constants.Neo4J.REL_CONFLICT)
@RelationshipProperties
public class PersonConflictLink implements FosRelationship {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @GeneratedValue
    @org.springframework.data.neo4j.core.schema.GeneratedValue
    Long graphId;

    String fosId;

    @EndNode
    @TargetNode
    FosEntity target;

    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    String relationshipType;
    String relationshipSubtype;

    @StartNode // only used by OGM
    PersonNode startNode;

    PersonConflictLink() {}

    public PersonConflictLink(
            String personId,
            FosEntity target, String relationshipType, String relationshipSubtype,
            String transactionId
    ) {
        this.fosId = DigestUtils.sha1Hex(String.format("%s_%s", personId, target.getFosId()));
        this.target = target;
        this.relationshipType = relationshipType;
        this.relationshipSubtype = relationshipSubtype;
        this.transactions.add(transactionId);
    }

    public PersonConflictLink(MnisMemberType memberType, DeclaredInterest interest) {
        this.fosId = DigestUtils.sha1Hex(String.format("%s_%s", mnisIdHash(memberType.getMemberId()), interest.getFosId()));
        this.target = interest;
    }

    public String getFosId() {
        return fosId;
    }

    public PersonConflictLink setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public FosEntity getTarget() {
        return target;
    }

    public PersonConflictLink setTarget(FosEntity target) {
        this.target = target;
        return this;
    }

    public ZonedDateTime getStartDT() {
        return startDT;
    }

    public PersonConflictLink setStartDT(ZonedDateTime startDT) {
        this.startDT = startDT;
        return this;
    }

    public ZonedDateTime getEndDT() {
        return endDT;
    }

    public PersonConflictLink setEndDT(ZonedDateTime endDT) {
        this.endDT = endDT;
        return this;
    }

    public Collection<String> getTransactions() {
        return transactions;
    }

    public PersonConflictLink setTransactions(Collection<String> transactions) {
        this.transactions = transactions;
        return this;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public PersonConflictLink setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }

    public String getRelationshipSubtype() {
        return relationshipSubtype;
    }

    public PersonConflictLink setRelationshipSubtype(String relationshipSubtype) {
        this.relationshipSubtype = relationshipSubtype;
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

        PersonConflictLink that = (PersonConflictLink) o;

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
        return "PersonConflictLink{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                '}';
    }

    public PersonConflictLink withStartNode(PersonNode personNode) {
        this.startNode = personNode;
        return this;
    }
}
