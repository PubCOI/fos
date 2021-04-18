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

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.springframework.stereotype.Service;

@Service
public class TransactionFactory {

    final ClientsGraphRepo clientsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;

    public TransactionFactory(ClientsGraphRepo clientsGraphRepo, OrganisationsGraphRepo organisationsGraphRepo) {
        this.clientsGraphRepo = clientsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
    }

    public LinkSourceToParentClient linkClientToParent(ClientNode source, ClientNode target, FosTransaction parentTransaction) {
        return new LinkSourceToParentClient(clientsGraphRepo, source, target, parentTransaction);
    }

    public CopyAllNoticesFromSourceToTarget copyNotices(ClientNode fromNode, ClientNode toNode) {
        return new CopyAllNoticesFromSourceToTarget(clientsGraphRepo, fromNode, toNode);
    }

    public HideNoticeRelationshipsFromClient hideRelPublished(ClientNode targetNode) {
        return new HideNoticeRelationshipsFromClient(clientsGraphRepo, targetNode);
    }

    public HideNode hideNode(ClientNode targetNode) {
        return new HideNode(clientsGraphRepo, targetNode);
    }

    public LinkOrganisationToParent linkOrgToParent(OrganisationNode o2c_fromNode, OrganisationNode o2c_toNode, FosTransaction parentTransaction) {
        return new LinkOrganisationToParent(organisationsGraphRepo, o2c_fromNode, o2c_toNode, parentTransaction);
    }
}
