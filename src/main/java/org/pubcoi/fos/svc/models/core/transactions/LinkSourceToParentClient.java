package org.pubcoi.fos.svc.models.core.transactions;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

public class LinkSourceToParentClient {

    private LinkSourceToParentClient() {}

    public static FosTransaction build(ClientNode source, ClientNode target, FosUser user) {
        return new FosTransaction()
                .setTransactionType(FosTransactionType.link_source_to_parent_clientNode)
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(user.getUid());
    }
}
