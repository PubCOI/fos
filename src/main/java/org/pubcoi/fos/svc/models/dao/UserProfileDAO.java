package org.pubcoi.fos.svc.models.dao;

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.fos.svc.models.core.FosUser;

public class UserProfileDAO {

    String displayName;
    String uid;

    public UserProfileDAO() {}

    public UserProfileDAO(FosUser user) {
        this.displayName = user.getDisplayName();
        this.uid = DigestUtils.sha1Hex(user.getUid());
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserProfileDAO setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public UserProfileDAO setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
