package org.pubcoi.fos.models.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fos_organisations")
public abstract class FOSOrganisation {
    @Id
    String id;

    public String getId() {
        return id;
    }

    FOSOrganisation() {
    }
}
