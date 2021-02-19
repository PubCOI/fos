package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.core.NodeReference;
import org.pubcoi.fos.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.models.core.transactions.FOSTransactionType;

import java.time.OffsetDateTime;

public class TransactionDAO {

    String id;
    FOSTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    OffsetDateTime transactionDT;

    public TransactionDAO(FOSTransaction transaction) {
        this.id = transaction.getId();
        this.transactionType = transaction.getTransactionType();
        this.source = transaction.getSource();
        this.target = transaction.getTarget();
        this.transactionDT = transaction.getTransactionDT();
    }

    public String getId() {
        return id;
    }

    public NodeReference getSource() {
        return source;
    }

    public NodeReference getTarget() {
        return target;
    }

    public OffsetDateTime getTransactionDT() {
        return transactionDT;
    }

    public FOSTransactionType getTransactionType() {
        return transactionType;
    }
}
