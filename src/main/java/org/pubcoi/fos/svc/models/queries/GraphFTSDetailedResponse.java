package org.pubcoi.fos.svc.models.queries;

import org.neo4j.driver.internal.value.ListValue;

public class GraphFTSDetailedResponse extends GraphFTSResponse {

    ListValue details;

    public ListValue getDetails() {
        return details;
    }

    public GraphFTSDetailedResponse setDetails(ListValue details) {
        this.details = details;
        return this;
    }
}
