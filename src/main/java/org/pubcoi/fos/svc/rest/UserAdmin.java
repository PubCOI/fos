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

package org.pubcoi.fos.svc.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointBadRequestException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.dto.UpdateProfileRequestDTO;
import org.pubcoi.fos.svc.models.dto.UpdateProfileResponseDTO;
import org.pubcoi.fos.svc.models.dto.UserLoginDTO;
import org.pubcoi.fos.svc.repos.mdb.FosUserRepo;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
public class UserAdmin {
    private static final Logger logger = LoggerFactory.getLogger(UserAdmin.class);

    final FosAuthProvider authProvider;

    public UserAdmin(FosAuthProvider authProvider, FosUserRepo userRepo) {
        this.authProvider = authProvider;
    }

    @PostMapping("/api/login")
    public void doLogin(@RequestBody UserLoginDTO loginDTO) {
        // shortcut .. if we already have the UID, we know we've created the user:
        // if no match, it could be because the UID refers to another provider (eg they initially
        // logged in via GitHub but are now using Google
        if (authProvider.existsByUid(loginDTO.getUid())) {
            authProvider.save(authProvider.getByUid(loginDTO.getUid()).setLastLogin(OffsetDateTime.now()));
            return;
        }

        // adds user meta if it doesn't exist
        try {
            UserRecord record = FirebaseAuth.getInstance().getUser(loginDTO.getUid());
            authProvider.save(new FosUser()
                    .setUid(loginDTO.getUid())
                    .setDisplayName(record.getDisplayName())
                    .setLastLogin(OffsetDateTime.now())
            );
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage(), e);
            throw new FosEndpointException();
        }
    }

    @PostMapping("/api/profile")
    public UpdateProfileResponseDTO getUserProfile(
            @RequestHeader("authToken") String authToken
    ) {
        String uid = authProvider.getUid(authToken);
        return new UpdateProfileResponseDTO(authProvider.getByUid(uid));
    }

    @PutMapping("/api/profile")
    public UpdateProfileResponseDTO updateUserProfile(
            @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO,
            @RequestHeader("authToken") String authToken
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        if (null == user) throw new FosEndpointBadRequestException("Unable to find user");
        return new UpdateProfileResponseDTO(authProvider.save(user.setDisplayName(updateProfileRequestDTO.getDisplayName())));
    }
}
