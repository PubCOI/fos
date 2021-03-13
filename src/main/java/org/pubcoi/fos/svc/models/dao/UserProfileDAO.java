package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.core.FosUser;

public class UserProfileDAO {

    String displayName;

    public UserProfileDAO() {}

    public UserProfileDAO(FosUser user) {
        this.displayName = user.getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserProfileDAO setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
