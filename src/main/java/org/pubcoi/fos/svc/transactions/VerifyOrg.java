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
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyOrg implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(VerifyOrg.class);

    final OrganisationsGraphRepo organisationsGraphRepo;
    final FosTransaction fosTransaction;
    OrganisationNode target;

    public VerifyOrg(OrganisationsGraphRepo organisationsGraphRepo,
                     OrganisationNode target,
                     FosTransaction fosTransaction) {
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.target = target;
        this.fosTransaction = fosTransaction;
    }

    @Override
    public FosTransaction exec() {
        logger.info("Verifying OrganisationNode {}", target);
        target.setVerified(true);
        organisationsGraphRepo.save(target);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.target = new OrganisationNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setTarget(new NodeReference(target));
    }
}
