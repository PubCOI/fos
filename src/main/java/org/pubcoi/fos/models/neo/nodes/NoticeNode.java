package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "notice")
public class NoticeNode {

    @Id // notice ID
    String id;

    Set<AwardNode> awards = new HashSet<>();

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

    NoticeNode setAwards(Set<AwardNode> awards) {
        this.awards = awards;
        return this;
    }
}
