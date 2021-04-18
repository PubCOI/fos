/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

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
