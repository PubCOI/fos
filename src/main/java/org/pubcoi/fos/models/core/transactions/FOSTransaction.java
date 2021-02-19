package org.pubcoi.fos.models.core.transactions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.models.core.NodeReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Document("fos_transactions")
public class FOSTransaction {

    @Id
    String id = UUID.randomUUID().toString();
    FOSTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    String uid;
    OffsetDateTime transactionDT = OffsetDateTime.now();
    String notes;

    // should only be built via helpers
    FOSTransaction() {}

    public String getId() {
        return id;
    }

    public FOSTransaction setId(String id) {
        this.id = id;
        return this;
    }

    public FOSTransactionType getTransactionType() {
        return transactionType;
    }

    public FOSTransaction setTransactionType(FOSTransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public NodeReference getSource() {
        return source;
    }

    public FOSTransaction setSource(NodeReference source) {
        this.source = source;
        return this;
    }

    public NodeReference getTarget() {
        return target;
    }

    public FOSTransaction setTarget(NodeReference target) {
        this.target = target;
        return this;
    }

    public OffsetDateTime getTransactionDT() {
        return transactionDT;
    }

    public FOSTransaction setTransactionDT(OffsetDateTime transactionDT) {
        this.transactionDT = transactionDT;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public FOSTransaction setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FOSTransaction that = (FOSTransaction) o;

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

    public String getUid() {
        return uid;
    }

    public FOSTransaction setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
