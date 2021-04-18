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
import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideNode implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(HideNode.class);

    final ClientsGraphRepo clientsGraphRepo;
    ClientNode clientNode;

    HideNode(
            ClientsGraphRepo clientsGraphRepo,
            FosEntity target
    ) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.clientNode = clientsGraphRepo.findClientHydratingNotices(target.getId()).orElseThrow();
    }

    @Override
    public FosTransaction exec() {
        if (null != clientNode.getParent() && null != clientNode.getParent().getClient()) {
            logger.debug("Hiding ClientNode {} (child of {})", clientNode.getId(), clientNode.getParent().getClient().getId());
        } else {
            logger.debug("Hiding ClientNode {}", clientNode.getId());
        }
        clientsGraphRepo.save(clientNode.setHidden(true));
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.clientNode = new ClientNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setTarget(new NodeReference(clientNode));
    }
}
