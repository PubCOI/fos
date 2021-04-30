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
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.relationships.OrgLELink;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.services.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkOrganisationToParent implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(LinkOrganisationToParent.class);

    final OrganisationsGraphRepo orgGraphRepo;
    OrganisationNode source;
    OrganisationNode target;
    final FosTransaction transaction;

    public LinkOrganisationToParent(
            OrganisationsGraphRepo orgGraphRepo,
            OrganisationNode source,
            OrganisationNode target,
            FosTransaction transaction
    ) {
        this.orgGraphRepo = orgGraphRepo;
        this.source = orgGraphRepo.findByFosIdHydratingPersons(source.getFosId()).orElseThrow();
        this.target = orgGraphRepo.findByFosIdHydratingPersons(target.getFosId()).orElseThrow();
        this.transaction = transaction;
    }

    @Override
    public FosTransaction exec() {
        logger.debug(Ansi.Blue.format("Linking %s to parent %s", source.getFosId(), target.getFosId()));
        source.setLegalEntity(new OrgLELink(
                source, target, transaction.id
        ));
        orgGraphRepo.save(source);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.source = new OrganisationNode(transaction.getSource());
        this.target = new OrganisationNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(transaction.getUid());
    }
}
