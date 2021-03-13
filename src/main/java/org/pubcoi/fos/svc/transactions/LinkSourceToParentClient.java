package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.pubcoi.fos.svc.models.neo.relationships.ClientParentClientLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkSourceToParentClient implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(LinkSourceToParentClient.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode source;
    ClientNode target;
    final FosTransaction transaction;

    LinkSourceToParentClient(
            ClientsGraphRepo clientsGraphRepo,
            FosEntity source,
            FosEntity target,
            FosTransaction transaction
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.source = clientsGraphRepo.findClientHydratingNotices(source.getId()).orElseThrow();
        this.target = clientsGraphRepo.findClientHydratingNotices(target.getId()).orElseThrow();
        this.transaction = transaction;
    }

    @Override
    public LinkSourceToParentClient fromTransaction(FosTransaction transaction) {
        this.source = new ClientNode(transaction.getSource());
        this.target = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(transaction.getUid());
    }

    @Override
    public FosTransaction exec() {
        logger.debug("Linking {} to parent {}", source.getId(), target.getId());
        source.setParent(new ClientParentClientLink(target, transaction));
        clientsGraphRepo.save(source);
        return getTransaction();
    }

    @Override
    public String toString() {
        return "LinkSourceToParentClient{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
