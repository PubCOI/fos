package org.pubcoi.fos.svc.models.dao.fts;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

public class ClientNodeFTSDAOResponse {

    String id;
    String name;
    Float score;

    public ClientNodeFTSDAOResponse() {}

    public ClientNodeFTSDAOResponse(GraphFTSResponse r) {
        this.id = r.getId();
        this.name = r.getName();
        this.score = (null != r.getScore()) ? r.getScore().asFloat() : null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientNodeFTSDAOResponse that = (ClientNodeFTSDAOResponse) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}
