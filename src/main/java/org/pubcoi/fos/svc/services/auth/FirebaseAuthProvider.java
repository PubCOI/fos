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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.pubcoi.fos.svc.exceptions.FosCoreException;
import org.pubcoi.fos.svc.exceptions.FosUnauthorisedResponseStatusException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.repos.mdb.FosUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"production", "firebase"})
@ConditionalOnMissingBean(NoAuthProvider.class)
@Service
public class FirebaseAuthProvider implements FosAuthProvider {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthProvider.class);

    final FosUserRepo userRepo;

    public FirebaseAuthProvider(FosUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void checkAuth(String authToken)  {
        try {
            FirebaseAuth.getInstance().verifyIdToken(authToken);
        } catch (FirebaseAuthException e) {
            throw new FosUnauthorisedResponseStatusException(e);
        }
    }

    @Override
    public String getUid(String authToken) {
        try {
            return getInstance(authToken).getUid();
        } catch (FosCoreException e) {
            throw new FosUnauthorisedResponseStatusException(e);
        }
    }

    @Override
    public FosUser getByUid(String uid) {
        // todo revisit this - should we actually instantiate the entire user object if it doesn't exist ie by
        // going back to the remote user db ...
        return userRepo.getByUid(uid);
    }

    @Override
    public boolean existsByUid(String uid) {
        // todo as above
        return userRepo.existsByUid(uid);
    }

    @Override
    public FosUser save(FosUser user) {
        return userRepo.save(user);
    }

    private FosUser getInstance(String authToken) throws FosCoreException {
        try {
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(authToken);
            if (!existsByUid(token.getUid())) {
                return userRepo.save(new FosUser(token));
            } else {
                return getByUid(token.getUid());
            }
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage(), e);
            throw new FosCoreException(e.getMessage(), e);
        }
    }
}
