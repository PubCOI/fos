package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.queries.ClientNodeFTSResponse;

public class ClientNodeFTSDAOResponse {

    String id;
    String clientName;
    Float score;

    public ClientNodeFTSDAOResponse() {}

    public ClientNodeFTSDAOResponse(ClientNodeFTSResponse r) {
        this.id = r.getId();
        this.clientName = r.getClientName();
        this.score = r.getScore().asFloat();
    }

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public Float getScore() {
        return score;
    }
}
