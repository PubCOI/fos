package org.pubcoi.fos.svc.models.core.transactions;

import org.pubcoi.fos.svc.models.core.FOSUser;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.springframework.lang.Nullable;

public class CanonicaliseClientNode {

    private CanonicaliseClientNode() {}

    public static FOSTransaction build(ClientNode target, FOSUser user, @Nullable String notes) {
        return new FOSTransaction()
                .setTransactionType(FOSTransactionType.mark_canonical_clientNode)
                .setTarget(new NodeReference(target))
                .setUid(user.getUid())
                .setNotes(notes);
    }

}
