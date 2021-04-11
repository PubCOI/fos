package org.pubcoi.fos.svc.models.neo.relationships;

import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;

@RelationshipProperties
public class OrgPersonLink {

    @TargetNode
    PersonNode person;
    ZonedDateTime startDT;
    ZonedDateTime endDT;
    Collection<String> transactions = new HashSet<>();

    OrgPersonLink() {}

    public OrgPersonLink(PersonNode person, ZonedDateTime startDT, ZonedDateTime endDT, String transactionId) {
        this.person = person;
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
}
