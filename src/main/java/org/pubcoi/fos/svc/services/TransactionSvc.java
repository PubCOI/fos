package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;

import java.util.List;

public interface TransactionSvc {

    boolean doTransaction(FOSTransaction transaction);
    boolean doTransaction(TransactionDAO transaction);

    List<TransactionDAO> getTransactions();

    void clearTransactions();
}
