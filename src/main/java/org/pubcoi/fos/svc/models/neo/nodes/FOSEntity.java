package org.pubcoi.fos.svc.models.neo.nodes;

public interface FOSEntity {
    String getId();
    Boolean getHidden();
    FOSEntity setHidden(Boolean hidden);
}
