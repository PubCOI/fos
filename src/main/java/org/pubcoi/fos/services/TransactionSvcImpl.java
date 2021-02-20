package org.pubcoi.fos.services;

import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.mdb.TransactionMDBRepo;
import org.pubcoi.fos.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.models.dao.TransactionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionSvcImpl implements TransactionSvc {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSvcImpl.class);

    TransactionMDBRepo transactionRepo;
    ClientsGraphRepo clientsGraphRepo;

    public TransactionSvcImpl(TransactionMDBRepo transactionRepo, ClientsGraphRepo clientsGraphRepo) {
        this.transactionRepo = transactionRepo;
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @Override
    public synchronized boolean doTransaction(FOSTransaction transaction) {
        switch (transaction.getTransactionType()) {
            case mark_canonical_clientNode:
                clientsGraphRepo.findById(transaction.getTarget().getId()).ifPresent(client -> {
                    clientsGraphRepo.save(client.setCanonical(true));
                });
                transactionRepo.save(transaction);
                return true;
            default:
                logger.warn("Unable to process transaction {}", transaction.getId());
        }
        return false;
    }

    @Override
    public boolean doTransaction(TransactionDAO transaction) {
        return false;
    }

    @Override
    public List<TransactionDAO> getTransactions() {
        return transactionRepo.findAll().stream()
                .sorted(Comparator.comparing(FOSTransaction::getTransactionDT))
                .map(TransactionDAO::new)
                .collect(Collectors.toList());
    }
}
