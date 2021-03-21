package org.pubcoi.fos.svc.models.queries;

import org.neo4j.driver.internal.value.FloatValue;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;

public class GraphFTSResponse {
    FosEntity entity;
    String id;
    String name;
    FloatValue score;

    public FloatValue getScore() {
        return score;
    }

    public GraphFTSResponse setScore(FloatValue score) {
        this.score = score;
        return this;
    }

    public String getId() {
        return id;
    }

    public GraphFTSResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GraphFTSResponse setName(String name) {
        this.name = name;
        return this;
    }

    public FosEntity getEntity() {
        return entity;
    }

    public GraphFTSResponse setEntity(FosEntity entity) {
        this.entity = entity;
        return this;
    }
}
