package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.dao.ClientNodeDAO;

public interface ClientsSvc {
    ClientNodeDAO getClientNode(String clientID);
}
