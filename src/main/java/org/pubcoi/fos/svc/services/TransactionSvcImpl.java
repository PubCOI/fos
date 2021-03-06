package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.FOSBadRequestException;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.gdb.NoticesGRepo;
import org.pubcoi.fos.svc.mdb.TransactionMDBRepo;
import org.pubcoi.fos.svc.models.core.transactions.FOSTransaction;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientParentClientLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionSvcImpl implements TransactionSvc {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSvcImpl.class);

    TransactionMDBRepo transactionRepo;
    NoticesGRepo noticesGRepo;
    ClientsGraphRepo clientsGraphRepo;

    public TransactionSvcImpl(TransactionMDBRepo transactionRepo, NoticesGRepo noticesGRepo, ClientsGraphRepo clientsGraphRepo) {
        this.transactionRepo = transactionRepo;
        this.noticesGRepo = noticesGRepo;
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @Override
    public synchronized boolean doTransaction(FOSTransaction transaction) {
        switch (transaction.getTransactionType()) {
            case link_source_to_parent_clientNode:
                // Don't do what I did and forget to hydrate the tenders
                Optional<ClientNode> source = clientsGraphRepo.findClientsHydratingTenders(transaction.getSource().getId());
                Optional<ClientNode> target = clientsGraphRepo.findClientsHydratingTenders(transaction.getTarget().getId());
                if (!source.isPresent() || !target.isPresent())
                    throw new FOSBadRequestException("Unable to resolve source and/or target");
                ClientNode sourceNode = source.get();
                ClientNode targetNode = target.get();

                if (!targetNode.getCanonical())
                    throw new FOSBadRequestException("Target is not a canonical ClientNode");

                logger.debug("Linking ClientNodes: {} to refer to parent {}", sourceNode.getId(), targetNode.getId());
                sourceNode.setParent(new ClientParentClientLink(targetNode, transaction));
                clientsGraphRepo.save(sourceNode);

                logger.debug("Linking all source notices directly to parent, marking with transaction ID {}", transaction.getId());
                sourceNode.getTenders().forEach(sourceNotice -> {
                    logger.debug("Transposing TenderNode:{} onto parent ClientNode:{}", sourceNotice.getId(), targetNode.getId());
                    targetNode.getTenders().add(sourceNotice
                            .setTransactionID(transaction.getId())
                    );
                });
                clientsGraphRepo.save(sourceNode);

                logger.info("Hiding all notices on the original node");
                sourceNode.getTenders().addAll(sourceNode.getTenders().stream()
                        .peek(n -> {
                            n.setHidden(true);
                        })
                        .collect(Collectors.toSet())
                );
                logger.info("Marking sourceNode {} as 'hidden'", sourceNode.getId());
                sourceNode.setHidden(true);
                logger.info("Committing sourceNode {}", sourceNode.getId());
                clientsGraphRepo.save(sourceNode);

                logger.info("Completed transaction {}", transaction.getId());
                transactionRepo.save(transaction);
                break;
            case mark_canonical_clientNode:
                clientsGraphRepo.findByIdEquals(transaction.getTarget().getId()).ifPresent(client -> {
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

    @Override
    public void clearTransactions() {
        transactionRepo.deleteAll();
    }
}
