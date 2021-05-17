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

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.services.ContractsFinderSvc;
import org.pubcoi.fos.svc.services.GraphSvc;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Profile({"production", "debug"})
@RestController
public class ProductionEndpoints {

    final FosAuthProvider authProvider;
    final ContractsFinderSvc contractsFinderSvc;
    final GraphSvc graphSvc;
    final CronRest cronRest;

    public ProductionEndpoints(FosAuthProvider authProvider,
                               ContractsFinderSvc contractsFinderSvc,
                               GraphSvc graphSvc,
                               CronRest cronRest) {
        this.authProvider = authProvider;
        this.contractsFinderSvc = contractsFinderSvc;
        this.graphSvc = graphSvc;
        this.cronRest = cronRest;
    }

    /**
     * Grabs a particular notice from the contract finder
     * @param noticeId the notice to add
     * @param authToken firebase user token
     * @return Result of adding notice
     */
    @PutMapping("/api/notices/{noticeId}")
    public FullNotice putNotice(@PathVariable String noticeId, @RequestHeader("authToken") String authToken) {
        authProvider.checkAuth(authToken);
        FullNotice notice = contractsFinderSvc.addNotice(noticeId);
        cronRest.addAttachmentsToMDB(notice);
        graphSvc.populateGraphFromMDB(noticeId);
        return notice;
    }

}
