package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideNode implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(HideNode.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode clientNode;

    HideNode(
            ClientsGraphRepo clientsGraphRepo,
            FosEntity target
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.clientNode = clientsGraphRepo.findClientHydratingNotices(target.getId()).orElseThrow();
    }

    @Override
    public FosTransaction exec() {
        if (null != clientNode.getParent() && null != clientNode.getParent().getClient()) {
            logger.debug("Hiding ClientNode {} (child of {})", clientNode.getId(), clientNode.getParent().getClient().getId());
        }
        else {
            logger.debug("Hiding ClientNode {}", clientNode.getId());
        }
        clientsGraphRepo.save(clientNode.setHidden(true));
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.clientNode = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setTarget(new NodeReference(clientNode));
    }
}
