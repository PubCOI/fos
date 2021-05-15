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

import org.pubcoi.fos.svc.models.dto.ApplicationStatsDTO;
import org.pubcoi.fos.svc.repos.es.AttachmentsESRepo;
import org.pubcoi.fos.svc.repos.es.MembersInterestsESRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.services.ApplicationStatusBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemRest {
    final ApplicationStatusBean applicationStatus;
    final AttachmentsESRepo attachmentsESRepo;
    final MembersInterestsESRepo membersInterestsESRepo;
    final PersonsGraphRepo personsGraphRepo;
    final AwardsMDBRepo awardsMDBRepo;

    public SystemRest(ApplicationStatusBean applicationStatus,
                      AttachmentsESRepo attachmentsESRepo,
                      MembersInterestsESRepo membersInterestsESRepo,
                      PersonsGraphRepo personsGraphRepo,
                      AwardsMDBRepo awardsMDBRepo) {
        this.applicationStatus = applicationStatus;
        this.attachmentsESRepo = attachmentsESRepo;
        this.membersInterestsESRepo = membersInterestsESRepo;
        this.personsGraphRepo = personsGraphRepo;
        this.awardsMDBRepo = awardsMDBRepo;
    }

    @GetMapping("/api/status")
    public ApplicationStatusBean getApplicationStatus() {
        return applicationStatus;
    }

    @GetMapping("/api/stats")
    public ApplicationStatsDTO getApplicationStats() {
        return new ApplicationStatsDTO(attachmentsESRepo.count(),
                membersInterestsESRepo.count(),
                personsGraphRepo.count(),
                awardsMDBRepo.count());
    }
}
