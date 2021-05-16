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

import com.opencorporates.schemas.OCOfficer__1;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.relationships.PersonConflictLink;
import org.pubcoi.fos.svc.services.Utils;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Node(primaryLabel = "Person")
public class PersonNode implements FosEntity {

    // if the person is a director, they will have an OCID and a UUID
    // if not, they will just have a UUID
    // a user CANNOT be assigned as an office bearer unless they have an OCID
    // todo: allow records to be merged

    @Id
    @GeneratedValue
    Long graphId;
    String fosId;
    Boolean hidden = false;
    String ocId;
    Integer parliamentaryId;
    String commonName;
    String occupation;
    String nationality;
    Set<String> transactions = new HashSet<>();
    @DynamicLabels
    Set<String> labels = new HashSet<>();
    @Relationship(Constants.Neo4J.REL_CONFLICT)
    List<PersonConflictLink> conflicts = new ArrayList<>();

    PersonNode() {
    }

    public PersonNode(PersonNodeType personNodeType, OCOfficer__1 officer, String transactionId) {
        this.ocId = convertPersonOCIdToString(officer.getId());
        this.fosId = generatePersonId(officer);
        this.labels.add(personNodeType.name());
        this.commonName = officer.getName();
        this.occupation = officer.getOccupation();
        this.nationality = officer.getNationality();
        this.transactions.add(transactionId);
    }

    public PersonNode(MnisMemberType memberType) {
        this.parliamentaryId = memberType.getMemberId();
        this.fosId = Utils.mnisIdHash(memberType.getMemberId());
        this.occupation = String.format("politician: Member_Id %d", memberType.getMemberId());
        this.labels.add(PersonNodeType.Politician.name());
        this.commonName = memberType.getFullTitle();
    }

    // used for replaying transactions
    public PersonNode(NodeReference node) {
        this.fosId = node.getFosId();
    }

    public static String generatePersonId(OCOfficer__1 officer) {
        return DigestUtils.sha1Hex(String.format("oc:%s", convertPersonOCIdToString(officer.getId())));
    }

    public static String convertPersonOCIdToString(Double openCorporatesId) {
        return String.format("%.0f", openCorporatesId);
    }

    public Integer getParliamentaryId() {
        return parliamentaryId;
    }

    public PersonNode setParliamentaryId(Integer parliamentaryId) {
        this.parliamentaryId = parliamentaryId;
        return this;
    }

    public String getFosId() {
        return fosId;
    }

    public PersonNode setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    @Override
    public Boolean isHidden() {
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

    public PersonNode addConflict(PersonConflictLink personConflictLink) {
        if (null == this.conflicts) this.conflicts = new ArrayList<>();
        this.conflicts.add(personConflictLink);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PersonNode that = (PersonNode) o;

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
        return "PersonNode{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                ", commonName='" + commonName + '\'' +
                '}';
    }

    public Long getGraphId() {
        return graphId;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public List<PersonConflictLink> getConflicts() {
        return conflicts;
    }
}
