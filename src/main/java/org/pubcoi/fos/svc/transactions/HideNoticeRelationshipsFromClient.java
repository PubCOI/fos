package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class HideNoticeRelationshipsFromClient implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(HideNoticeRelationshipsFromClient.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode targetClient;

    HideNoticeRelationshipsFromClient(
            ClientsGraphRepo clientsGraphRepo,
            FosEntity target
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.targetClient = clientsGraphRepo.findClientHydratingNotices(target.getId()).orElseThrow();
    }


    @Override
    public FosTransaction exec() {
        logger.debug("Hiding ClientNode->Notice relationships from client {}", targetClient.getId());

        targetClient.setNotices(
                targetClient.getNoticeRelationships().stream()
                        .peek(rel -> rel.setHidden(true))
                        .collect(Collectors.toList())
        );

        clientsGraphRepo.save(targetClient);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.targetClient = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setTarget(new NodeReference(targetClient));
    }
}
