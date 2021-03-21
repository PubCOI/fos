package org.pubcoi.fos.svc.models.neo.nodes;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.cf.FullNotice;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "Notice")
public class NoticeNode implements FosEntity {

    @Id // notice ID
    String id;

    Set<AwardNode> awards = new HashSet<>();

    Boolean hidden = false;

    public NoticeNode() {}

    public NoticeNode(FullNotice notice) {
        this.id = notice.getId();
    }

    public String getId() {
        return id;
    }

    public NoticeNode setId(String id) {
        this.id = id;
        return this;
    }

    public NoticeNode addAward(AwardNode awardNode) {
        this.awards.add(awardNode);
        return this;
    }

    public Set<AwardNode> getAwards() {
        return awards;
    }

    public NoticeNode setAwards(Set<AwardNode> awards) {
        this.awards = awards;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NoticeNode that = (NoticeNode) o;

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
        return "NoticeNode{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public NoticeNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
