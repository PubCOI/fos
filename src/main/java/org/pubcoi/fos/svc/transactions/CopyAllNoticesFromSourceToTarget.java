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

import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyAllNoticesFromSourceToTarget implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(CopyAllNoticesFromSourceToTarget.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode fromClient;
    ClientNode toClient;

    CopyAllNoticesFromSourceToTarget(
            ClientsGraphRepo clientsGraphRepo,
            ClientNode fromNode,
            ClientNode toNode
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.fromClient = clientsGraphRepo.findClientHydratingNotices(fromNode.getId()).orElseThrow();
        this.toClient = clientsGraphRepo.findClientHydratingNotices(toNode.getId()).orElseThrow();
    }

    @Override
    public FosTransaction exec() {
        logger.debug("{} currently has {} notices; {} has {}",
                fromClient.getId(), fromClient.getNoticeRelationships().size(),
                toClient.getId(), toClient.getNoticeRelationships().size()
        );

        for (ClientNoticeLink notice : fromClient.getNoticeRelationships()) {
            if (toClient.getNoticeRelationships().contains(notice)) {
                logger.warn("Notice {} already exists on ClientNode {}", notice.getId(), toClient.getId());
            } else {
                toClient.getNoticeRelationships().add(notice);
                logger.debug("Added notice {} to ClientNode {}", notice.getId(), toClient.getId());
            }
        }

        clientsGraphRepo.save(toClient);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.fromClient = new ClientNode(transaction.getSource());
        this.toClient = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(fromClient))
                .setTarget(new NodeReference(toClient));
    }
}
