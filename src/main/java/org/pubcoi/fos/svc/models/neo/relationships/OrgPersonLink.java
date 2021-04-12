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
public class OrgPersonLink {

    @Id
    String id;

    @TargetNode
    PersonNode person;
    String position;
    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    OrgPersonLink() {}

    public OrgPersonLink(PersonNode person, String companyId, String position, ZonedDateTime startDT, ZonedDateTime endDT, String transactionId) {
        this.id = DigestUtils.sha1Hex(String.format("%s_%s_%s", companyId, person.getId(), position));
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

    public String getId() {
        return id;
    }

    public OrgPersonLink setId(String id) {
        this.id = id;
        return this;
    }
}
