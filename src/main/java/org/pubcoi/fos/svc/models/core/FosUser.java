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

package org.pubcoi.fos.svc.models.core;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

/**
 * Fos site users.
 * UID refers to a Firebase UID.
 */
@Document("fos_users")
public class FosUser {

    @Id
    String uid;
    String displayName;
    OffsetDateTime lastLogin;

    public FosUser() {
    }

    public FosUser(FirebaseToken token) {
        this.uid = token.getUid();
        this.displayName = token.getName();
        this.lastLogin = OffsetDateTime.now();
    }

    public String getDisplayName() {
        return displayName;
    }

    public FosUser setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public FosUser setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public OffsetDateTime getLastLogin() {
        return lastLogin;
    }

    public FosUser setLastLogin(OffsetDateTime lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    @Override
    public String toString() {
        return "FosUser{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
