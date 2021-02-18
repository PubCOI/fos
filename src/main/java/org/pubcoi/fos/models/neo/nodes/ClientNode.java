package org.pubcoi.fos.models.neo.nodes;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node(primaryLabel = "client")
public class ClientNode {

    @Id
    String id;

    @Relationship("AKA")
    ClientNode client;

    Boolean verified;

    @Relationship("PUBLISHED")
    Set<NoticeNode> notices;

}
