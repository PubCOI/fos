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
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.neo.relationships.PersonConflictLink;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LinkPersonToOrg implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(LinkPersonToOrg.class);

    final PersonsGraphRepo personsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final FosTransaction fosTransaction;
    PersonNode source;
    OrganisationNode target;

    public LinkPersonToOrg(PersonsGraphRepo personsGraphRepo, OrganisationsGraphRepo organisationsGraphRepo, PersonNode source, OrganisationNode target, FosTransaction parentTransaction) {
        this.personsGraphRepo = personsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.source = source;
        this.target = target;
        this.fosTransaction = parentTransaction;
    }

    @Override
    public FosTransaction exec() {
        logger.info("Linking PersonNode {} to OrgNode {}", source, target);
        source.addConflict(new PersonConflictLink(source.getFosId(), target, "Member Interest", null, UUID.randomUUID().toString()));
        personsGraphRepo.save(source);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.source = new PersonNode(transaction.getSource());
        this.target = new OrganisationNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target));
    }
}
