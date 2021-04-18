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

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.fos.svc.models.neo.relationships.PersonConflictLink;
import org.pubcoi.fos.svc.services.Utils;
import org.springframework.data.neo4j.core.schema.DynamicLabels;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node(primaryLabel = "Person")
public class PersonNode implements FosEntity {

    // if the person is a director, they will have an OCID and a UUID
    // if not, they will just have a UUID
    // a user CANNOT be assigned as an office bearer unless they have an OCID
    // todo: allow records to be merged

    @Id
    String id;
    Boolean hidden = false;
    String ocId;
    Integer parliamentaryId;
    String commonName;
    String occupation;
    String nationality;
    Set<String> transactions = new HashSet<>();
    @DynamicLabels
    Set<String> labels = new HashSet<>();
    @Relationship("CONFLICT")
    List<PersonConflictLink> conflicts = new ArrayList<>();

    PersonNode() {}

    public PersonNode(PersonNodeType personNodeType, String openCorporatesId, String name, String occupation, String nationality, String transactionId) {
        this.ocId = openCorporatesId;
        this.id = DigestUtils.sha1Hex(String.format("oc:%s", openCorporatesId));
        this.labels.add(personNodeType.name());
        this.commonName = name;
        this.occupation = occupation;
        this.nationality = nationality;
        this.transactions.add(transactionId);
    }

    public PersonNode(MnisMemberType memberType) {
        this.parliamentaryId = memberType.getMemberId();
        this.id = Utils.parliamentaryId(memberType.getMemberId());
        this.occupation = String.format("politician: Member_Id %d", memberType.getMemberId());
        this.labels.add(PersonNodeType.Politician.name());
        this.commonName = memberType.getFullTitle();
    }

    public PersonNode(String name) {
        this.id = UUID.randomUUID().toString();
        this.commonName = name;
    }

    public Integer getParliamentaryId() {
        return parliamentaryId;
    }

    public PersonNode setParliamentaryId(Integer parliamentaryId) {
        this.parliamentaryId = parliamentaryId;
        return this;
    }

    public String getId() {
        return id;
    }

    public PersonNode setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public FosEntity setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getOcId() {
        return ocId;
    }

    public PersonNode setOcId(String ocId) {
        this.ocId = ocId;
        return this;
    }

    public String getCommonName() {
        return commonName;
    }

    public PersonNode setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public PersonNode setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getNationality() {
        return nationality;
    }

    public PersonNode setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public Set<String> getTransactions() {
        return transactions;
    }

    public PersonNode setTransactions(Set<String> transactions) {
        this.transactions = transactions;
        return this;
    }

    public List<PersonConflictLink> getConflicts() {
        return conflicts;
    }

    public PersonNode setConflicts(List<PersonConflictLink> conflicts) {
        this.conflicts = conflicts;
        return this;
    }

    @Override
    public String toString() {
        return "PersonNode{" +
                "id='" + id + '\'' +
                ", hidden=" + hidden +
                ", ocId='" + ocId + '\'' +
                ", commonName='" + commonName + '\'' +
                ", occupation='" + occupation + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}
