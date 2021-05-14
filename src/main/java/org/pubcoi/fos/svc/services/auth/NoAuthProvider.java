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

package org.pubcoi.fos.svc.services.auth;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.services.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Profile({"standalone"})
@Service
public class NoAuthProvider implements FosAuthProvider {
    private static final Logger logger = LoggerFactory.getLogger(NoAuthProvider.class);
    private static final String STANDALONE_UID = "__STANDALONE__";

    private FosUser generateStandaloneUser() {
        return new FosUser().setDisplayName("STANDALONE").setLastLogin(OffsetDateTime.now()).setUid(STANDALONE_UID);
    }

    @Override
    public void checkAuth(String authToken) {
        logger.warn(Ansi.Yellow.colorize("Standalone profile active: Token is valid by default (no checks performed on token validity)"));
    }

    @Override
    public String getUid(String authToken) {
        return STANDALONE_UID;
    }

    @Override
    public FosUser getByUid(String uid) {
        return generateStandaloneUser();
    }

    @Override
    public boolean existsByUid(String uid) {
        return true;
    }

    @Override
    public FosUser save(FosUser setLastLogin) {
        return generateStandaloneUser();
    }
}
