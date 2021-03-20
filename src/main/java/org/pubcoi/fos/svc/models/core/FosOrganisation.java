package org.pubcoi.fos.svc.models.core;

import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fos_organisations")
public abstract class FosOrganisation implements FosEntity {
    @Id
    String id;

    Boolean hidden = false;

    public String getId() {
        return id;
    }

    FosOrganisation() {
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public FosOrganisation setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
