package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.gdb.NoticesGraphRepo;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.mdb.TransactionMDBRepo;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.transactions.FosTransaction;
import org.pubcoi.fos.svc.transactions.TransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionOrchestrationImpl implements TransactionOrchestrationSvc {
    private static final Logger logger = LoggerFactory.getLogger(TransactionOrchestrationImpl.class);

    final TransactionFactory tcf;
    final TransactionMDBRepo transactionRepo;
    final NoticesGraphRepo noticesGRepo;
    final ClientsGraphRepo clientsGraphRepo;
    final OrganisationsGraphRepo orgGraphRepo;

    TransactionOrchestrationImpl(
            TransactionFactory tcf,
            TransactionMDBRepo transactionRepo,
            NoticesGraphRepo noticesGRepo,
            ClientsGraphRepo clientsGraphRepo,
            OrganisationsGraphRepo orgGraphRepo) {
        this.tcf = tcf;
        this.transactionRepo = transactionRepo;
        this.noticesGRepo = noticesGRepo;
        this.clientsGraphRepo = clientsGraphRepo;
        this.orgGraphRepo = orgGraphRepo;
    }

    @Override
    @Transactional
    public synchronized boolean exec(FosTransaction metaTransaction) {
        switch (metaTransaction.getTransactionType()) {
            case link_source_to_parent_clientNode:
                ClientNode s2p_fromNode = clientsGraphRepo.findClientHydratingNotices(
                        metaTransaction.getSource().getId()).orElseThrow();
                ClientNode s2p_toNode = clientsGraphRepo.findClientHydratingNotices(
                        metaTransaction.getTarget().getId()).orElseThrow();

                if (!s2p_toNode.getCanonical()) {
                    throw new FosBadRequestException("Parent ClientNode is not canonical");
                }
                if (s2p_fromNode.getCanonical()) {
                    throw new FosBadRequestException("Child ClientNode cannot be canonical");
                }

                transactionRepo.save(tcf.linkClientToParent(s2p_fromNode, s2p_toNode, metaTransaction).exec().withMeta(metaTransaction));
                transactionRepo.save(tcf.copyNotices(s2p_fromNode, s2p_toNode).exec().withMeta(metaTransaction));
                transactionRepo.save(tcf.hideRelPublished(s2p_fromNode).exec().withMeta(metaTransaction));
                transactionRepo.save(tcf.hideNode(s2p_fromNode).exec().withMeta(metaTransaction));

                logger.info("Completed transaction {}", metaTransaction.getId());
                transactionRepo.save(metaTransaction);
                break;

            case mark_canonical_clientNode:
                clientsGraphRepo.findClientHydratingNotices(
                        metaTransaction.getTarget().getId()).ifPresent(client -> {
                            clientsGraphRepo.save(client.setCanonical(true));
                        }
                );
                transactionRepo.save(metaTransaction);
                return true;

            case link_org_to_canonical:
                OrganisationNode o2c_fromNode = orgGraphRepo
                        .findOrgHydratingPersons(metaTransaction.getSource().getId())
                        .orElse(orgGraphRepo.findOrgNotHydratingPersons(metaTransaction.getSource().getId()).orElseThrow());
                OrganisationNode o2c_toNode = orgGraphRepo
                        .findOrgHydratingPersons(metaTransaction.getTarget().getId())
                        .orElse(orgGraphRepo.findOrgNotHydratingPersons(metaTransaction.getTarget().getId()).orElseThrow());

                if (!o2c_toNode.isVerified()) {
                    throw new FosBadRequestException("Target node must be verified");
                }

                transactionRepo.save(tcf.linkOrgToParent(o2c_fromNode, o2c_toNode, metaTransaction).exec().withMeta(metaTransaction));
                return true;

            default:
                logger.warn("Unable to process transaction {}", metaTransaction.getId());
        }
        return false;
    }

    @Override
    public boolean exec(TransactionDAO transaction) {
        return false;
    }

    @Override
    public List<TransactionDAO> getTransactions() {
        return transactionRepo.findAll().stream()
                .sorted(Comparator.comparing(FosTransaction::getTransactionDT))
                .map(TransactionDAO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void clearTransactions() {
        transactionRepo.deleteAll();
    }
}
