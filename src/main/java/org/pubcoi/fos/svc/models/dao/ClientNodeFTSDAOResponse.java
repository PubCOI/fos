package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

public class ClientNodeFTSDAOResponse {

    String id;
    String name;
    Float score;

    public ClientNodeFTSDAOResponse() {}

    public ClientNodeFTSDAOResponse(GraphFTSResponse r) {
        this.id = r.getId();
        this.name = r.getName();
        this.score = r.getScore().asFloat();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Float getScore() {
        return score;
    }
}
