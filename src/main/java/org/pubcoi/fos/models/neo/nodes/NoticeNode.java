package org.pubcoi.fos.models.neo.nodes;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node(primaryLabel = "notice")
public class NoticeNode {

    @Id
    String id;

}
