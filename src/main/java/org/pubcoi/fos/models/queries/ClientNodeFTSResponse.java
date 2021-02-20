package org.pubcoi.fos.models.queries;

import org.neo4j.driver.internal.value.FloatValue;
import org.pubcoi.fos.models.neo.nodes.ClientNode;

public class ClientNodeFTSResponse {
    ClientNode client;
    String id;
    String clientName;
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

    public String getClientName() {
        return clientName;
    }

    public ClientNodeFTSResponse setClientName(String clientName) {
        this.clientName = clientName;
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
