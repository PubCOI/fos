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
