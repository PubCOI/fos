package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactory {

    final ClientsGraphRepo clientsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;

    public TransactionFactory(ClientsGraphRepo clientsGraphRepo, OrganisationsGraphRepo organisationsGraphRepo) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
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

    public LinkOrganisationToParent linkOrgToParent(OrganisationNode o2c_fromNode, OrganisationNode o2c_toNode, FosTransaction parentTransaction) {
        return new LinkOrganisationToParent(organisationsGraphRepo, o2c_fromNode, o2c_toNode, parentTransaction);
    }
}
