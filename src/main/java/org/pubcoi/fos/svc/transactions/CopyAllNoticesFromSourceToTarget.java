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

import org.pubcoi.fos.svc.exceptions.core.FosCoreRuntimeException;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
        this.fromClient = clientsGraphRepo.findClientHydratingNotices(fromNode.getFosId()).orElseThrow();
        this.toClient = clientsGraphRepo.findClientHydratingNotices(toNode.getFosId()).orElseThrow();
    }

    @Override
    public FosTransaction exec() {
        logger.debug("{} currently has {} notices; {} has {}",
                fromClient.getFosId(), (null == fromClient.getNotices()) ? 0 : fromClient.getNotices().size(),
                toClient.getFosId(), toClient.getNotices().size()
        );

        if (null == fromClient.getNotices()) throw new FosCoreRuntimeException("FromClient Notices is null");

        for (ClientNoticeLink notice : fromClient.getNotices()) {
            if (null != toClient && null != toClient.getNotices() && toClient.getNotices().contains(notice)) {
                logger.warn("Notice {} already exists on ClientNode {}", notice.getFosId(), toClient.getFosId());
            } else {
                Objects.requireNonNull(toClient).addNotice(notice);
                logger.debug("Added notice {} to ClientNode {}", notice.getFosId(), toClient.getFosId());
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
