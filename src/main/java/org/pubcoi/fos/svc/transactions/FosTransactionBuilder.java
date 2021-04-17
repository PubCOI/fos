package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.lang.Nullable;

public class FosTransactionBuilder {

    private FosTransactionBuilder() {
    }

    public static FosTransaction markCanonicalNode(
            ClientNode target,
            FosUser currentUser,
            @Nullable String notes
    ) {
        return new FosTransaction()
                .setTransactionType(FosTransactionType.mark_canonical_clientNode)
                .setTarget(new NodeReference(target))
                .setUid(currentUser.getUid())
                .setNotes(notes);
    }

    public static FosTransaction linkSourceToParent(
            ClientNode source,
            ClientNode target,
            FosUser currentUser,
            @Nullable String notes
    ) {
        return new FosTransaction()
                .setTransactionType(FosTransactionType.link_source_to_parent_clientNode)
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(currentUser.getUid())
                .setNotes(notes);
    }

    public static FosTransaction resolveCompany(
            OrganisationNode source,
            OrganisationNode target,
            FosUser currentUser,
            @Nullable String notes
    ) {
        return new FosTransaction()
                .setTransactionType(FosTransactionType.link_org_to_canonical)
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(currentUser.getUid())
                .setNotes(notes);
    }
}
