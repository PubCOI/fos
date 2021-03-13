package org.pubcoi.fos.svc.models.core.transactions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Document("fos_transactions")
public class FosTransaction {

    @Id
    String id = UUID.randomUUID().toString();
    FosTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    String uid;
    OffsetDateTime transactionDT = OffsetDateTime.now();
    String notes;

    // should only be built via helpers
    FosTransaction() {}

    public String getId() {
        return id;
    }

    public FosTransaction setId(String id) {
        this.id = id;
        return this;
    }

    public FosTransactionType getTransactionType() {
        return transactionType;
    }

    public FosTransaction setTransactionType(FosTransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public NodeReference getSource() {
        return source;
    }

    public FosTransaction setSource(NodeReference source) {
        this.source = source;
        return this;
    }

    public NodeReference getTarget() {
        return target;
    }

    public FosTransaction setTarget(NodeReference target) {
        this.target = target;
        return this;
    }

    public OffsetDateTime getTransactionDT() {
        return transactionDT;
    }

    public FosTransaction setTransactionDT(OffsetDateTime transactionDT) {
        this.transactionDT = transactionDT;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public FosTransaction setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FosTransaction that = (FosTransaction) o;

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

    public FosTransaction setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
