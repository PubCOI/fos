package org.pubcoi.fos.services;

import org.pubcoi.fos.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.models.dao.TransactionDAO;

import java.util.List;

public interface TransactionSvc {

    boolean doTransaction(FOSTransaction transaction);
    boolean doTransaction(TransactionDAO transaction);

    List<TransactionDAO> getTransactions();

    void clearTransactions();
}
