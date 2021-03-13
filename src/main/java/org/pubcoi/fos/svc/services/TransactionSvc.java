package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.transactions.FosTransaction;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;

import java.util.List;

public interface TransactionSvc {

    boolean doTransaction(FosTransaction transaction);
    boolean doTransaction(TransactionDAO transaction);

    List<TransactionDAO> getTransactions();

    void clearTransactions();
}
