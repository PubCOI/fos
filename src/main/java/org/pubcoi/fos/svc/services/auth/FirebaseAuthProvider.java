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
import org.pubcoi.fos.svc.exceptions.FosUnauthorisedResponseStatusException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.repos.mdb.FosUserRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"production", "firebase"})
@ConditionalOnMissingBean(NoAuthProvider.class)
@Service
public class FirebaseAuthProvider implements FosAuthProvider {

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
            return FirebaseAuth.getInstance().verifyIdToken(authToken).getUid();
        } catch (FirebaseAuthException e) {
            throw new FosUnauthorisedResponseStatusException(e);
        }
    }

    @Override
    public FosUser getByUid(String uid) {
        return userRepo.getByUid(uid);
    }

    @Override
    public boolean existsByUid(String uid) {
        return userRepo.existsByUid(uid);
    }

    @Override
    public FosUser save(FosUser user) {
        return userRepo.save(user);
    }
}
