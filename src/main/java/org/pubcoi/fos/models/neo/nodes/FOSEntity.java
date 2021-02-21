package org.pubcoi.fos.models.neo.nodes;

public interface FOSEntity {
    String getId();
    Boolean getHidden();
    FOSEntity setHidden(Boolean hidden);
}
