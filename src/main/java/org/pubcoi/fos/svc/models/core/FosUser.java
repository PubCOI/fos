package org.pubcoi.fos.svc.models.core;

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
