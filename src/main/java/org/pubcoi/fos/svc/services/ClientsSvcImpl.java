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

package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.ItemNotFoundException;
import org.pubcoi.fos.svc.models.dto.ClientNodeDTO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.springframework.stereotype.Service;

@Service
public class ClientsSvcImpl implements ClientsSvc {

    final ClientsGraphRepo clientsGraphRepo;

    public ClientsSvcImpl(ClientsGraphRepo clientsGraphRepo, NoticesMDBRepo noticesMDBRepo) {
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @Override
    public ClientNodeDTO getClientNodeDTO(String clientID) {
        return new ClientNodeDTO(getClientNode(clientID));
    }

    @Override
    public ClientNode getClientNode(String clientID) {
        return clientsGraphRepo.findClientHydratingNotices(clientID).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    public void save(ClientNode client) {
        clientsGraphRepo.save(client);
    }
}
