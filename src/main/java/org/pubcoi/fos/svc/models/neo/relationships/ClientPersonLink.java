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
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;

@RelationshipProperties
public class ClientPersonLink implements FosRelationship {

    @Id
    String id;

    @TargetNode
    PersonNode person;

    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    String relationshipType;
    String relationshipSubtype;

    ClientPersonLink() {}

    public ClientPersonLink(
            PersonNode person,
            String relationshipType, String relationshipSubtype,
            String clientId, String transactionId
    ) {
        this.id = DigestUtils.sha1Hex(String.format("%s_%s", clientId, person.getId()));
        this.person = person;
        this.transactions.add(transactionId);
    }

    public String getId() {
        return id;
    }

    public ClientPersonLink setId(String id) {
        this.id = id;
        return this;
    }

    public PersonNode getPerson() {
        return person;
    }

    public ClientPersonLink setPerson(PersonNode person) {
        this.person = person;
        return this;
    }

    public ZonedDateTime getStartDT() {
        return startDT;
    }

    public ClientPersonLink setStartDT(ZonedDateTime startDT) {
        this.startDT = startDT;
        return this;
    }

    public ZonedDateTime getEndDT() {
        return endDT;
    }

    public ClientPersonLink setEndDT(ZonedDateTime endDT) {
        this.endDT = endDT;
        return this;
    }

    public Collection<String> getTransactions() {
        return transactions;
    }

    public ClientPersonLink setTransactions(Collection<String> transactions) {
        this.transactions = transactions;
        return this;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public ClientPersonLink setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }

    public String getRelationshipSubtype() {
        return relationshipSubtype;
    }

    public ClientPersonLink setRelationshipSubtype(String relationshipSubtype) {
        this.relationshipSubtype = relationshipSubtype;
        return this;
    }
}
