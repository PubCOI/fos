package org.pubcoi.fos.svc.models.dao.fts;

import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

public class PersonNodeFTSDAOResponse {
    String id;
    String name;
    Float score;

    public PersonNodeFTSDAOResponse() {}

    public PersonNodeFTSDAOResponse(GraphFTSResponse r) {
        this.id = r.getId();
        this.name = r.getName();
        this.score = (null != r.getScore()) ? r.getScore().asFloat() : null;
    }

    public String getId() {
        return id;
    }

    public PersonNodeFTSDAOResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonNodeFTSDAOResponse setName(String name) {
        this.name = name;
        return this;
    }

    public Float getScore() {
        return score;
    }

    public PersonNodeFTSDAOResponse setScore(Float score) {
        this.score = score;
        return this;
    }
}
