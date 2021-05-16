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

import org.pubcoi.fos.svc.repos.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.services.BatchExecutorSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Profile("batch")
@RestController
public class CronRest {

    private static final Logger logger = LoggerFactory.getLogger(CronRest.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final BatchExecutorSvc batchExecutorSvc;

    @Value("${fos.cron.key:DEFAULT}")
    String cronKey;

    public CronRest(AttachmentMDBRepo attachmentMDBRepo, BatchExecutorSvc batchExecutorSvc) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.batchExecutorSvc = batchExecutorSvc;
    }

    @GetMapping("/api/cron/batch")
    public void runBatchJobs(@RequestHeader("api-token") String apiToken) {
        if (cronKey.equals("DEFAULT")) {
            logger.error("fos.cron.key must be set to something other than default");
            return;
        }
        if (!apiToken.equals(cronKey)) {
            logger.error("key is incorrect");
            return;
        }
        attachmentMDBRepo.findAll().stream()
                .filter(a -> null != a.getS3Locations() && a.getS3Locations().size() > 0)
                .limit(1)
                .forEach(attachment -> {
                    try {
                        batchExecutorSvc.runBatch(attachment);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }

}
