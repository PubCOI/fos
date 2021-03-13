package org.pubcoi.fos.svc.models.core.transactions;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.springframework.lang.Nullable;

public class CanonicaliseClientNode {

    private CanonicaliseClientNode() {}

    public static FosTransaction build(ClientNode target, FosUser user, @Nullable String notes) {
        return new FosTransaction()
                .setTransactionType(FosTransactionType.mark_canonical_clientNode)
                .setTarget(new NodeReference(target))
                .setUid(user.getUid())
                .setNotes(notes);
    }

}
