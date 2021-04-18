package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyAllNoticesFromSourceToTarget implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(CopyAllNoticesFromSourceToTarget.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode fromClient;
    ClientNode toClient;

    CopyAllNoticesFromSourceToTarget(
            ClientsGraphRepo clientsGraphRepo,
            ClientNode fromNode,
            ClientNode toNode
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.fromClient = clientsGraphRepo.findClientHydratingNotices(fromNode.getId()).orElseThrow();
        this.toClient = clientsGraphRepo.findClientHydratingNotices(toNode.getId()).orElseThrow();
    }

    @Override
    public FosTransaction exec() {
        logger.debug("{} currently has {} notices; {} has {}",
                fromClient.getId(), fromClient.getNoticeRelationships().size(),
                toClient.getId(), toClient.getNoticeRelationships().size()
        );

        for (ClientNoticeLink notice : fromClient.getNoticeRelationships()) {
            if (toClient.getNoticeRelationships().contains(notice)) {
                logger.warn("Notice {} already exists on ClientNode {}", notice.getId(), toClient.getId());
            }
            else {
                toClient.getNoticeRelationships().add(notice);
                logger.debug("Added notice {} to ClientNode {}", notice.getId(), toClient.getId());
            }
        }

        clientsGraphRepo.save(toClient);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.fromClient = new ClientNode(transaction.getSource());
        this.toClient = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(fromClient))
                .setTarget(new NodeReference(toClient));
    }
}
