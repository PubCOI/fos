package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.transactions.FosTransaction;
import org.pubcoi.fos.svc.transactions.FosTransactionType;

import java.time.OffsetDateTime;

public class TransactionDAO {

    String id;
    FosTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    OffsetDateTime transactionDT;

    public TransactionDAO(FosTransaction transaction) {
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

    public FosTransactionType getTransactionType() {
        return transactionType;
    }
}
