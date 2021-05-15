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
import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRecordNotFoundException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointBadRequestException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.dto.UpdateProfileRequestDTO;
import org.pubcoi.fos.svc.models.dto.UpdateProfileResponseDTO;
import org.pubcoi.fos.svc.models.dto.UserLoginDTO;
import org.pubcoi.fos.svc.models.mdb.UserObjectFlag;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.repos.mdb.UserObjectFlagRepo;
import org.pubcoi.fos.svc.services.ApplicationStatusBean;
import org.pubcoi.fos.svc.services.ScheduledSvc;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
public class UserAdminRest {
    private static final Logger logger = LoggerFactory.getLogger(UserAdminRest.class);

    final FosAuthProvider authProvider;
    final UserObjectFlagRepo userObjectFlagRepo;
    final OCCompaniesRepo ocCompaniesRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final ScheduledSvc scheduledSvc;
    final ApplicationStatusBean applicationStatus;

    public UserAdminRest(FosAuthProvider authProvider,
                         UserObjectFlagRepo userObjectFlagRepo,
                         OCCompaniesRepo ocCompaniesRepo,
                         OrganisationsGraphRepo organisationsGraphRepo,
                         ScheduledSvc scheduledSvc, ApplicationStatusBean applicationStatus) {
        this.authProvider = authProvider;
        this.userObjectFlagRepo = userObjectFlagRepo;
        this.ocCompaniesRepo = ocCompaniesRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.scheduledSvc = scheduledSvc;
        this.applicationStatus = applicationStatus;
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

    @PutMapping("/api/ui/flags/{type}/{objectId}")
    public String updateFlag(
            @RequestHeader("authToken") String authToken,
            @PathVariable String objectId,
            @PathVariable String type
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        userObjectFlagRepo.save(new UserObjectFlag(objectId, user));
        if (type.equals("organisation")) {
            // if item is not already in db, add it
            if (!ocCompaniesRepo.existsById(objectId)) {
                // todo - put into separate thread
                try {
                    scheduledSvc.getCompany(objectId);
                } catch (FosCoreRecordNotFoundException e) {
                    logger.info(String.format("Unable to find company record: %s", e.getMessage()));
                }
            }
            // should now be in MDB repo, check if it's in graph
            if (ocCompaniesRepo.existsById(objectId)) {
                OCCompanySchema companySchema = ocCompaniesRepo.findById(objectId).orElseThrow();
                if (!organisationsGraphRepo.existsByJurisdictionAndReference(companySchema.getJurisdictionCode(), companySchema.getCompanyNumber())) {
                    OrganisationNode node = organisationsGraphRepo.save(new OrganisationNode(companySchema));
                    logger.info("Created new org {} in graph", node);
                }
            }
        }
        return String.format("Flagged item %s", objectId);
    }

    @DeleteMapping("/api/ui/flags/{type}/{objectId}")
    public String deleteFlag(
            @RequestHeader("authToken") String authToken,
            @PathVariable String objectId,
            @PathVariable String type
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        userObjectFlagRepo.delete(new UserObjectFlag(objectId, user));
        return String.format("Removed flag on item %s", objectId);
    }

    @GetMapping("/api/status")
    public ApplicationStatusBean getApplicationStatus() {
        return applicationStatus;
    }

}
