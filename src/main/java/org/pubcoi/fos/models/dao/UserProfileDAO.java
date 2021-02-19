package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.core.FOSUser;

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
