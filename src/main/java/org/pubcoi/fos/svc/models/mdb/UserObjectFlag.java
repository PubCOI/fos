/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

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
