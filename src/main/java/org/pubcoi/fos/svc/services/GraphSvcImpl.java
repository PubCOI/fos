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

package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.cdm.batch.BatchJob;
import org.pubcoi.cdm.batch.BatchJobTypeEnum;
import org.pubcoi.cdm.cf.AdditionalDetailType;
import org.pubcoi.cdm.fos.AttachmentFactory;
import org.pubcoi.cdm.fos.BatchJobFactory;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.core.FosTaskType;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.repos.gdb.jpa.AwardsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.NoticesGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GraphSvcImpl implements GraphSvc {
    private static final Logger logger = LoggerFactory.getLogger(GraphSvcImpl.class);

    AwardsMDBRepo awardsMDBRepo;
    OrganisationsMDBRepo organisationsMDBRepo;
    OCCompaniesRepo ocCompaniesRepo;
    AwardsGraphRepo awardsGraphRepo;
    NoticesMDBRepo noticesMDBRepo;
    OrganisationsGraphRepo orgGraphRepo;
    ClientsGraphRepo clientsGraphRepo;
    NoticesGraphRepo noticesGRepo;
    ScheduledSvc scheduledSvc;
    TasksSvc tasksSvc;
    AttachmentMDBRepo attachmentMDBRepo;
    BatchJobMDBRepo batchJobMDBRepo;

    public GraphSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            AwardsGraphRepo awardsGraphRepo,
            OrganisationsMDBRepo organisationsMDBRepo,
            OrganisationsGraphRepo orgGraphRepo,
            OCCompaniesRepo ocCompaniesRepo,
            NoticesMDBRepo noticesMDBRepo,
            ClientsGraphRepo clientsGraphRepo,
            NoticesGraphRepo noticesGRepo,
            ScheduledSvc scheduledSvc,
            TasksSvc tasksSvc,
            AttachmentMDBRepo attachmentMDBRepo,
            BatchJobMDBRepo batchJobMDBRepo
    ) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.awardsGraphRepo = awardsGraphRepo;
        this.organisationsMDBRepo = organisationsMDBRepo;
        this.orgGraphRepo = orgGraphRepo;
        this.ocCompaniesRepo = ocCompaniesRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.clientsGraphRepo = clientsGraphRepo;
        this.noticesGRepo = noticesGRepo;
        this.scheduledSvc = scheduledSvc;
        this.tasksSvc = tasksSvc;
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.batchJobMDBRepo = batchJobMDBRepo;
    }

    @Override
    public void clearGraphs() {
        clientsGraphRepo.deleteAll();
        awardsGraphRepo.deleteAll();
        noticesGRepo.deleteAll();
        orgGraphRepo.deleteAll();
    }

    /**
     * This is just a convenience function for now ...
     */
    @Override
    public void populateGraphFromMDB() {
        // add all clients
        noticesMDBRepo.findAll().forEach(notice -> {
            Optional<ClientNode> nodeOpt = (clientsGraphRepo.findClientHydratingNotices(ClientNode.resolveId(notice)));
            if (nodeOpt.isPresent()) {
                logger.debug("Using already instantiated client node {}", ClientNode.resolveId(notice));
            }
            ClientNode node = (nodeOpt.orElseGet(() -> {
                ClientNode clientNode = new ClientNode(notice);
                tasksSvc.createTask(new DRTask(FosTaskType.resolve_client, clientNode));
                return clientNode;
            }));
            node.addNotice(notice); // FIXME - this doesn't check whether notice exists already
            clientsGraphRepo.save(node);
        });

        // add all awards
        awardsMDBRepo.findAll().forEach(award -> { // fixme is adding rels
            if (null != award.getFosOrganisation()) {
                FosOrganisation org = award.getFosOrganisation();
                try {
                    // if the organisation is "verified", it'll already have an entry on the OCCompanyRepo
                    Optional<OCCompanySchema> ocCompany = ocCompaniesRepo.findById(org.getId());
                    boolean verified = ocCompany.isPresent();
                    // if it's verified, use the OFFICIAL name for the company (not whatever is in Contracts Finder)
                    String companyName = (verified) ? ocCompany.orElseThrow().getName() : award.getSupplierName();

                    OrganisationNode orgNode = orgGraphRepo.findById(org.getId()).orElse(
                            new OrganisationNode(org)
                    );
                    logger.debug("Saving org node: {}", orgNode);
                    orgGraphRepo.save(orgNode);

                    AwardNode awardNode = awardsGraphRepo.findById(award.getId()).orElse(new AwardNode()
                            .setId(award.getId())
                            .setValue(award.getValue())
                            .setNoticeId(award.getNoticeId())
                            .setGroupAward(award.getGroup())
                            .setOrganisation(
                                    orgGraphRepo.findById(org.getId()).orElseThrow(() -> new FosException()),
                                    award.getAwardedDate().toZonedDateTime(),
                                    award.getStartDate().toZonedDateTime(),
                                    award.getEndDate().toZonedDateTime()
                            )
                    );
                    logger.debug("Saving node: {}", awardNode);
                    awardsGraphRepo.save(awardNode);
                } catch (FosException e) {
                    logger.error("Unable to insert entry into graph: is source MDB fully populated?");
                }
            } else {
                logger.debug("Unable to find OC entry for {}", award.getSupplierName());
            }
        });

        // for every notice in the mongo db, put the attachments onto the attachments DB
        noticesMDBRepo.findAll().forEach(notice -> {
            for (AdditionalDetailType additionalDetail : notice.getAdditionalDetails().getDetailsList()) {
                // note that each notice will have an "additional details" object that is exactly the
                // same as the description on the notice
                // helpfully, the ID and notice ID on these objects is the same
                if (additionalDetail.getId().equals(additionalDetail.getNoticeId())) continue;
                if (!attachmentMDBRepo.existsById(additionalDetail.getId())) {
                    attachmentMDBRepo.save(AttachmentFactory.build(additionalDetail));
                }
            }
        });

        // for every attachment, create a job
        attachmentMDBRepo.findAll().forEach(attachment -> {
            BatchJob dl = null;
            if (!batchJobMDBRepo.existsByTargetIdAndType(attachment.getId(), BatchJobTypeEnum.DOWNLOAD)) {
                dl = batchJobMDBRepo.save(BatchJobFactory.build(attachment, BatchJobTypeEnum.DOWNLOAD));
            }
            if (!batchJobMDBRepo.existsByTargetIdAndType(attachment.getId(), BatchJobTypeEnum.PROCESS_OCR)) {
                batchJobMDBRepo.save(BatchJobFactory.build(attachment, BatchJobTypeEnum.PROCESS_OCR).withDepends((null != dl ? dl.getId() : null)));
            }
        });

        // for every award on the graph, link the associated notice
        awardsGraphRepo.findAllNotHydrating()
                .forEach(award -> {
                    logger.debug("Attempting to add backwards ref for notice {} to AwardNode {}", award.getNoticeId(), award.getId());
                    noticesGRepo.findById(award.getNoticeId()).ifPresent(notice -> {
                        logger.debug("Found notice {}", notice);
                        logger.debug("Awards size: {}", notice.getAwards().size());
                        if (!notice.getAwards().contains(award)) {
                            logger.debug("Adding award {} to notice {}", award.getId(), notice.getId());
                            noticesGRepo.save(notice.addAward(award));
                        } else {
                            logger.debug("Did not add {} to {} (already exists)", award.getId(), notice.getId());
                        }
                    });
                });
    }

}
