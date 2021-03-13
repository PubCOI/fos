package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.transactions.FosTransaction;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;

import java.util.List;

/*
 Responsible for taking transactions, building the instances / transaction handlers
 and then enabling callers to hit 'exec' when ready ...
 */
public interface TransactionOrchestrationSvc {

    boolean exec(FosTransaction metaTransaction);

    boolean exec(TransactionDAO transaction);

    List<TransactionDAO> getTransactions();

    void clearTransactions();
}
