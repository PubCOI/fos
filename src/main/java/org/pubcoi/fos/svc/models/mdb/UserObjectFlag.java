package org.pubcoi.fos.svc.models.mdb;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import java.time.OffsetDateTime;

@Document(collection = "fos_user_object_flags")
public class UserObjectFlag {

    @Id
    String id;
    OffsetDateTime created;
    String uid;
    String entityId;

    UserObjectFlag() {}

    public UserObjectFlag(String objectId, FosUser user) {
        this.id = String.format("%s:%s", user.getUid(), objectId);
        this.created = OffsetDateTime.now();
        this.uid = user.getUid();
        this.entityId = objectId;
    }

    public String getId() {
        return id;
    }

    public UserObjectFlag setId(String id) {
        this.id = id;
        return this;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public UserObjectFlag setCreated(OffsetDateTime created) {
        this.created = created;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public UserObjectFlag setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public UserObjectFlag setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }
}
