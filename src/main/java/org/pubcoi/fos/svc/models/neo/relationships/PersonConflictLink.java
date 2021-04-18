package org.pubcoi.fos.svc.models.neo.relationships;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.fos.svc.models.neo.nodes.DeclaredInterest;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;

import static org.pubcoi.fos.svc.services.Utils.parliamentaryId;

@RelationshipProperties
public class PersonConflictLink {

    @Id
    String id;

    @TargetNode
    FosEntity target;

    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    String relationshipType;
    String relationshipSubtype;

    PersonConflictLink() {}

    public PersonConflictLink(
            String personId,
            FosEntity target, String relationshipType, String relationshipSubtype,
            String transactionId
    ) {
        this.id = DigestUtils.sha1Hex(String.format("%s_%s", personId, target.getId()));
        this.target = target;
        this.relationshipType = relationshipType;
        this.relationshipSubtype = relationshipSubtype;
        this.transactions.add(transactionId);
    }

    public PersonConflictLink(MnisMemberType memberType, DeclaredInterest interest) {
        this.id = DigestUtils.sha1Hex(String.format("%s_%s", parliamentaryId(memberType.getMemberId()), interest.getId()));
        this.target = interest;
    }

    public String getId() {
        return id;
    }

    public PersonConflictLink setId(String id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PersonConflictLink that = (PersonConflictLink) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PersonConflictLink{" +
                "id='" + id + '\'' +
                ", target=" + target +
                ", relationshipType='" + relationshipType + '\'' +
                ", relationshipSubtype='" + relationshipSubtype + '\'' +
                '}';
    }
}
