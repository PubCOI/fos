package org.pubcoi.fos.models.neo.relationships;

import org.pubcoi.fos.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;

@RelationshipProperties
public class ClientParentClientLink {

    @Id
    String id;

    @TargetNode
    ClientNode client;

    ZonedDateTime transactionDT;

    String transactionID;

    Boolean hidden = false;

    ClientParentClientLink() {}

    public ClientParentClientLink(ClientNode target, FOSTransaction transaction) {
        this.id = DigestUtils.md5DigestAsHex(String.format("%s:%s", transaction.getId(), target.getId()).getBytes());
        this.transactionID = transaction.getId();
        this.transactionDT = transaction.getTransactionDT().toZonedDateTime();
        this.client = target;
    }

    public String getId() {
        return id;
    }

    public ClientNode getClient() {
        return client;
    }

    public ZonedDateTime getTransactionDT() {
        return transactionDT;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ClientParentClientLink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
