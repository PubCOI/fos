package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactory {

    final ClientsGraphRepo clientsGraphRepo;

    public TransactionFactory(ClientsGraphRepo clientsGraphRepo) {
        this.clientsGraphRepo = clientsGraphRepo;
    }

    public LinkSourceToParentClient linkClientToParent(ClientNode source, ClientNode target, FosTransaction parentTransaction) {
        return new LinkSourceToParentClient(clientsGraphRepo, source, target, parentTransaction);
    }

    public CopyAllNoticesFromSourceToTarget copyNotices(ClientNode fromNode, ClientNode toNode) {
        return new CopyAllNoticesFromSourceToTarget(clientsGraphRepo, fromNode, toNode);
    }

    public HideNoticeRelationshipsFromClient hideRelPublished(ClientNode targetNode) {
        return new HideNoticeRelationshipsFromClient(clientsGraphRepo, targetNode);
    }

    public HideNode hideNode(ClientNode targetNode) {
        return new HideNode(clientsGraphRepo, targetNode);
    }
}
