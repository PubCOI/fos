package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.queries.GraphFTSDetailedResponse;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

import java.util.ArrayList;
import java.util.List;

public class GraphDetailedSearchResponseDAO extends GraphSearchResponseDAO {

    List<String> details = new ArrayList<>();

    public GraphDetailedSearchResponseDAO(GraphFTSDetailedResponse response, NodeTypeEnum nodeTypeEnum) {
        super(response, nodeTypeEnum);
        for (Object o : response.getDetails().asList()) {
            if (o instanceof String) {
                details.add((String) o);
            }
        }
    }

    public GraphDetailedSearchResponseDAO(GraphFTSResponse response, NodeTypeEnum nodeTypeEnum) {
        super(response, nodeTypeEnum);
    }

    public List<String> getDetails() {
        return details;
    }

    public GraphDetailedSearchResponseDAO setDetails(List<String> details) {
        this.details = details;
        return this;
    }
}
