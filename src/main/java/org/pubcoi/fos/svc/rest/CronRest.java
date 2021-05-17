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

import org.pubcoi.cdm.batch.BatchJob;
import org.pubcoi.cdm.batch.BatchJobTypeEnum;
import org.pubcoi.cdm.cf.AdditionalDetailType;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.fos.AttachmentFactory;
import org.pubcoi.cdm.fos.BatchJobFactory;
import org.pubcoi.fos.svc.repos.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.BatchJobMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.services.BatchExecutorSvc;
import org.pubcoi.fos.svc.services.BatchRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

@Profile("batch")
@RestController
public class CronRest {

    private static final Logger logger = LoggerFactory.getLogger(CronRest.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final BatchExecutorSvc batchExecutorSvc;
    final NoticesMDBRepo noticesMDBRepo;
    final BatchJobMDBRepo batchJobMDBRepo;
    final BatchRepo batchRepo;

    @Value("${fos.cron.key:DEFAULT}")
    String cronKey;

    public CronRest(AttachmentMDBRepo attachmentMDBRepo,
                    BatchExecutorSvc batchExecutorSvc,
                    NoticesMDBRepo noticesMDBRepo,
                    BatchJobMDBRepo batchJobMDBRepo, BatchRepo batchRepo) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.batchExecutorSvc = batchExecutorSvc;
        this.noticesMDBRepo = noticesMDBRepo;
        this.batchJobMDBRepo = batchJobMDBRepo;
        this.batchRepo = batchRepo;
    }

    @GetMapping("/api/cron/execute-batch")
    public void runBatchJobs(@RequestHeader("api-token") String apiToken, @RequestHeader(value = "count", required = false, defaultValue = "1") String count) {
        if (cronKey.equals("DEFAULT")) {
            logger.error("fos.cron.key must be set to something other than default");
            return;
        }
        if (!apiToken.equals(cronKey)) {
            logger.error("key is incorrect");
            return;
        }
        attachmentMDBRepo.findAll().stream()
                .filter(a -> null == a.getS3Locations() || a.getS3Locations().size() == 0)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(Integer.parseInt(count))
                .forEach(attachment -> {
                    try {
                        batchExecutorSvc.runBatch(attachment);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }

    @GetMapping("/api/cron/update-attachments")
    public void createAndUpdateAttachmentObjects() {
        logger.info("Extracting all attachment metadata from notices");
        noticesMDBRepo.findAll().forEach(this::addAttachmentsToMDB);
        logger.info("Creating default batch jobs for attachment processing");
        attachmentMDBRepo.findAll().forEach(this::createDefaultBatchJobs);
    }

    private void createDefaultBatchJobs(Attachment attachment) {
        BatchJob dl = null;
        if (!batchJobMDBRepo.existsByTargetIdAndType(attachment.getId(), BatchJobTypeEnum.DOWNLOAD)) {
            dl = batchJobMDBRepo.save(BatchJobFactory.build(attachment, BatchJobTypeEnum.DOWNLOAD));
        }
        if (!batchJobMDBRepo.existsByTargetIdAndType(attachment.getId(), BatchJobTypeEnum.PROCESS_OCR)) {
            batchJobMDBRepo.save(BatchJobFactory.build(attachment, BatchJobTypeEnum.PROCESS_OCR).withDepends((null != dl ? dl.getId() : null)));
        }
    }

    private void addAttachmentsToMDB(FullNotice notice) {
        for (AdditionalDetailType additionalDetail : notice.getAdditionalDetails().getDetailsList()) {
            // note that each notice will have an "additional details" object that is exactly the
            // same as the description on the notice
            // helpfully, the ID and notice ID on these objects is the same
            if (additionalDetail.getId().equals(additionalDetail.getNoticeId())) continue;
            if (!attachmentMDBRepo.existsById(additionalDetail.getId())) {
                attachmentMDBRepo.save(AttachmentFactory.build(additionalDetail));
            }
        }
    }

}
