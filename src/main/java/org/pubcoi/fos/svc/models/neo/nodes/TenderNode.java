package org.pubcoi.fos.svc.models.neo.nodes;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "Tender")
public class TenderNode implements FOSEntity {

    @Id // tender ID
    String id;

    Set<AwardNode> awards = new HashSet<>();

    Boolean hidden = false;

    public TenderNode() {}

    public TenderNode(FullNotice notice) {
        this.id = notice.getId();
    }

    public String getId() {
        return id;
    }

    public TenderNode setId(String id) {
        this.id = id;
        return this;
    }

    public TenderNode addAward(AwardNode awardNode) {
        this.awards.add(awardNode);
        return this;
    }

    public Set<AwardNode> getAwards() {
        return awards;
    }

    public TenderNode setAwards(Set<AwardNode> awards) {
        this.awards = awards;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TenderNode that = (TenderNode) o;

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

    @Override
    public String toString() {
        return "TenderNode{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public TenderNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
