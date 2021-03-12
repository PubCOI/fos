package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.ItemNotFoundException;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.models.dao.ClientNodeDAO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.springframework.stereotype.Service;

@Service
public class ClientsSvcImpl implements ClientsSvc {

    final ClientsGraphRepo clientsGraphRepo;

    public ClientsSvcImpl(ClientsGraphRepo clientsGraphRepo, NoticesMDBRepo noticesMDBRepo) {
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @Override
    public ClientNodeDAO getClientNode(String clientID) {
        ClientNode client = clientsGraphRepo.findClientHydratingNotices(clientID).orElseThrow(ItemNotFoundException::new);
        return new ClientNodeDAO(client);
    }
}
