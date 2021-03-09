package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.core.FOSUser;

public class UserProfileDAO {

    String displayName;

    public UserProfileDAO() {}

    public UserProfileDAO(FOSUser user) {
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
