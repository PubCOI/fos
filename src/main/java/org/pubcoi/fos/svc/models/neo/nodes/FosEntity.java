package org.pubcoi.fos.svc.models.neo.nodes;

public interface FosEntity {
    String getId();
    Boolean getHidden();
    FosEntity setHidden(Boolean hidden);
}
