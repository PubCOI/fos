package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.dao.ClientNodeDAO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

public interface ClientsSvc {
    ClientNodeDAO getClientNodeDAO(String clientID);
    ClientNode getClientNode(String clientID);

    void save(ClientNode client);
}
