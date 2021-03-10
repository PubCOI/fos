package org.pubcoi.fos.svc.models.queries;

import org.neo4j.driver.internal.value.FloatValue;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

public class ClientNodeFTSResponse {
    ClientNode client;
    String id;
    String name;
    FloatValue score;

    public FloatValue getScore() {
        return score;
    }

    public ClientNodeFTSResponse setScore(FloatValue score) {
        this.score = score;
        return this;
    }

    public String getId() {
        return id;
    }

    public ClientNodeFTSResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClientNodeFTSResponse setName(String name) {
        this.name = name;
        return this;
    }

    public ClientNode getClient() {
        return client;
    }

    public ClientNodeFTSResponse setClient(ClientNode client) {
        this.client = client;
        return this;
    }
}
