package org.pubcoi.fos.svc.models.dao;

public class UpdateProfileRequestDAO {
    String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public UpdateProfileRequestDAO setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
