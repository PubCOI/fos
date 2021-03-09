package org.pubcoi.fos.svc.models.core.transactions;

import org.pubcoi.fos.svc.models.core.FOSUser;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

public class LinkSourceToParentClient {

    private LinkSourceToParentClient() {}

    public static FOSTransaction build(ClientNode source, ClientNode target, FOSUser user) {
        return new FOSTransaction()
                .setTransactionType(FOSTransactionType.link_source_to_parent_clientNode)
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(user.getUid());
    }
}
