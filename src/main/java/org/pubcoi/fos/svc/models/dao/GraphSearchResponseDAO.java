package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

public class GraphSearchResponseDAO extends ClientNodeFTSDAOResponse {
    public GraphSearchResponseDAO() {
    }

    NodeTypeEnum type;

    public GraphSearchResponseDAO(GraphFTSResponse response, NodeTypeEnum typeEnum) {
        super(response);
        this.type = typeEnum;
    }

    public NodeTypeEnum getType() {
        return type;
    }

    public GraphSearchResponseDAO setType(NodeTypeEnum type) {
        this.type = type;
        return this;
    }
}
