package org.pubcoi.fos.models.core;

import org.pubcoi.fos.models.neo.nodes.FOSEntity;

public class NodeReference {
    String nodeType;
    String id;

    public NodeReference(FOSEntity node) {
        this.nodeType = node.getClass().getTypeName();
        this.id = node.getId();
    }

    public String getNodeType() {
        return nodeType;
    }

    public NodeReference setNodeType(String nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public String getId() {
        return id;
    }

    public NodeReference setId(String id) {
        this.id = id;
        return this;
    }
}
