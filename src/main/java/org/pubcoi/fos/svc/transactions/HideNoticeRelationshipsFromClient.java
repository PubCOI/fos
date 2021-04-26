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
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class HideNoticeRelationshipsFromClient implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(HideNoticeRelationshipsFromClient.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode targetClient;

    HideNoticeRelationshipsFromClient(
            ClientsGraphRepo clientsGraphRepo,
            FosEntity target
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.targetClient = clientsGraphRepo.findClientHydratingNotices(target.getFosId()).orElseThrow();
    }


    @Override
    public FosTransaction exec() {
        logger.debug("Hiding ClientNode->Notice relationships from client {}", targetClient.getFosId());

        targetClient.setNotices(
                targetClient.getNoticeRelationships().stream()
                        .peek(rel -> rel.setHidden(true))
                        .collect(Collectors.toList())
        );

        clientsGraphRepo.save(targetClient);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.targetClient = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setTarget(new NodeReference(targetClient));
    }
}
