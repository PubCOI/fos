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

import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.mnis.MnisMembersType;
import org.pubcoi.cdm.pw.PWRootType;
import org.pubcoi.cdm.pw.RegisterEntryType;
import org.pubcoi.fos.svc.exceptions.core.FosCoreException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointUnauthException;
import org.pubcoi.fos.svc.repos.mdb.MnisMembersRepo;
import org.pubcoi.fos.svc.services.MnisSvc;
import org.pubcoi.fos.svc.services.XslSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Admin endpoints ; basically any that require a key to run ...
 */
@RestController
public class AdminRest {
    private static final Logger logger = LoggerFactory.getLogger(AdminRest.class);
    public static final String AUTH_HEADER = "api-token";
    public static final String API_KEY_VAR = "fos.api.key";

    final XslSvc xslSvc;
    final MnisSvc mnisSvc;
    final RestTemplate restTemplate;
    final MnisMembersRepo mnisMembersRepo;

    @Value("${" + API_KEY_VAR + ":DEFAULT}")
    String apiKey;

    @Value("${pubcoi.fos.apis.parliament.commons-list}")
    String commonsDataURL;

    @Value("${pubcoi.fos.apis.parliament.lords-list}")
    String lordsDataURL;

    public AdminRest(XslSvc xslSvc, MnisSvc mnisSvc, RestTemplate restTemplate, MnisMembersRepo mnisMembersRepo) {
        this.xslSvc = xslSvc;
        this.mnisSvc = mnisSvc;
        this.restTemplate = restTemplate;
        this.mnisMembersRepo = mnisMembersRepo;
    }

    /**
     * Used to do an initial load of politician data (names etc)
     */
    @PutMapping("/api/admin/interests/bootstrap")
    public void bootstrapPolData(
            @RequestHeader(AUTH_HEADER) String apiToken
    ) {
        checkAuth(apiToken);

        logger.info("Loading data from commons endpoint");
        loadMembersData(commonsDataURL);

        logger.info("Loading data from lords endpoint");
        loadMembersData(lordsDataURL);

        logger.info("Done");
    }

    /**
     * This is taking data from PublicWhip data dumps
     *
     * @param input   uploaded regmem xml (see README)
     * @param dataset must be one of the valid regmem files ... this is checked against list in application
     *                properties ... basically so we can trace the data back to wherever we got it but
     *                don't pollute the db with invalid data sources
     * @return OK if the data was successfully loaded in
     */
    @PostMapping("/api/admin/interests/upload")
    public String uploadMembersInterests(
            @RequestHeader(AUTH_HEADER) String apiToken,
            @RequestBody String input,
            @RequestParam("dataset") String dataset) {
        checkAuth(apiToken);
        PWRootType cleaned = xslSvc.cleanPWData(input);
        for (RegisterEntryType register : cleaned.getRegisters()) {
            try {
                mnisSvc.addInterestsToMDB(register, dataset);
            } catch (FosCoreException e) {
                throw new FosEndpointException();
            }
        }
        return "ok";
    }

    /**
     * Responsible for extracting data from the text eg whether it's a donation etc
     */
    @PostMapping("/api/admin/interests/reanalyse")
    public void reanalyseInterests(
            @RequestHeader(AUTH_HEADER) String apiToken
    ) {
        checkAuth(apiToken);
        mnisSvc.reanalyse();
    }

    /**
     * Responsible for writing analysed interests onto the ES instance
     */
    @PostMapping("/api/admin/interests/reindex")
    public void reindexInterests(
            @RequestHeader(AUTH_HEADER) String apiToken
    ) {
        checkAuth(apiToken);
        mnisSvc.reindex();
    }

    /**
     * Used while bootstrapping the system with members data
     *
     * @param dataUrl Lords or commons datasource / URL
     */
    private void loadMembersData(String dataUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<MnisMembersType> commonsResponse = restTemplate
                .exchange(dataUrl, HttpMethod.GET, entity, MnisMembersType.class);
        if (null != commonsResponse.getBody()) {
            for (MnisMemberType member : commonsResponse.getBody().getMembers()) {
                if (!mnisMembersRepo.existsById(member.getMemberId())) {
                    mnisMembersRepo.save(member);
                }
            }
        }
    }

    public void checkAuth(String apiToken) {
        if (apiKey.equals("DEFAULT")) {
            logger.error("fos.cron.key must be set to something other than default");
            throw new FosEndpointUnauthException();
        }
        if (!apiToken.equals(apiKey)) {
            logger.error("key is incorrect");
            throw new FosEndpointUnauthException();
        }
    }
}
