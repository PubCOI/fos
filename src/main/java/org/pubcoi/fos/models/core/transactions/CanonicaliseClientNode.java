package org.pubcoi.fos.models.core.transactions;

import org.pubcoi.fos.models.core.FOSUser;
import org.pubcoi.fos.models.core.NodeReference;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.springframework.lang.Nullable;

public class CanonicaliseClientNode {

    public static FOSTransaction build(ClientNode target, FOSUser user, @Nullable String notes) {
        return new FOSTransaction()
                .setTransactionType(FOSTransactionType.mark_canonical_clientNode)
                .setTarget(new NodeReference(target))
                .setUser(user)
                .setNotes(notes);
    }

}
