package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node(primaryLabel = "notice")
public class NoticeNode {

    @Id // notice ID
    String id;

    public NoticeNode() {}

    public NoticeNode(FullNotice notice) {
        this.id = notice.getId();
    }
}
