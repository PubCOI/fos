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
import org.pubcoi.fos.svc.models.neo.nodes.NoticeNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.relationships.AwardOrgLink;
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

    private void addAllClientsAndNoticesToGraph() {
        noticesMDBRepo.findAll().forEach(notice -> {
            Optional<ClientNode> nodeOpt = (clientsGraphRepo.findClientHydratingNotices(ClientNode.resolveId(notice)));
            if (nodeOpt.isPresent()) {
                logger.debug("Using already instantiated client node {}", ClientNode.resolveId(notice));
            }
            ClientNode client = (nodeOpt.orElseGet(() -> {
                ClientNode clientNode = new ClientNode(notice);
                tasksSvc.createTask(new DRTask(FosTaskType.resolve_client, clientNode));
                return clientNode;
            }));
            NoticeNode noticeNode = (noticesGRepo.findByFosId(notice.getId()).orElse(new NoticeNode(notice)));
            if (!clientsGraphRepo.relationshipExists(client.getFosId(), noticeNode.getFosId())) {
                client.addNotice(noticeNode, notice.getId(), notice.getNotice().getPublishedDate().toZonedDateTime());
            }
            clientsGraphRepo.save(client);
        });
    }

    /**
     * This is just a convenience function for now ...
     */
    @Override
    public void populateGraphFromMDB() {

        addAllClientsAndNoticesToGraph();

        // add all awards
        awardsMDBRepo.findAll().forEach(award -> {
            if (null != award.getFosOrganisation()) {
                FosOrganisation org = award.getFosOrganisation();
                try {
                    logger.trace("Looking up OrganisationNode {}", org.getFosId());
                    final OrganisationNode orgNode = orgGraphRepo.findByFosId(org.getFosId()).orElseGet(() -> {
                                logger.trace(Ansi.Yellow.format("Did not find OrganisationNode %s in graph, instantiating new instance", org.getFosId()));
                                return new OrganisationNode(org);
                            }
                    );

                    logger.trace("Looking up AwardNode {}", award.getId());
                    AwardNode awardNode = awardsGraphRepo.findByFosIdHydratingAwardees(award.getId()).orElseGet(() -> {
                                logger.trace(Ansi.Yellow.format("Did not find AwardNode %s in graph, instantiating new instance", award.getId()));
                                return new AwardNode()
                                        .setFosId(award.getId())
                                        .setValue(award.getValue())
                                        .setNoticeId(award.getNoticeId())
                                        .setGroupAward(award.getGroup());
                            }
                    );

                    // check if award<->org exists ... if not create it
                    if (!awardsGraphRepo.relationshipExists(awardNode.getFosId(), orgNode.getFosId())) {
                        logger.trace(Ansi.Yellow.format("Relationship between award %s and org %s does not exist: linking nodes", awardNode.getFosId(), orgNode.getFosId()));
                        AwardOrgLink awLink = new AwardOrgLink(orgNode, award.getAwardedDate().toLocalDate(), award.getStartDate().toLocalDate(), award.getEndDate().toLocalDate());
                        awardNode.getAwardees().add(awLink);
                        logger.debug("Saved {}", awLink);
                    }

                    orgGraphRepo.save(orgNode);
                    awardsGraphRepo.save(awardNode);
                    logger.debug("Saved {}", awardNode);
                } catch (FosException e) {
                    logger.error(Ansi.Red.colorize("Unable to insert entry into graph: is source MDB fully populated?"), e);
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
                    logger.debug("Attempting to add backwards ref for notice {} to AwardNode {}", award.getNoticeId(), award.getFosId());
                    noticesGRepo.findByFosId(award.getNoticeId()).ifPresent(notice -> {
                        logger.debug("Found notice {}", notice);

                        // fixme
                        if (!notice.getAwards().contains(award)) {
                            logger.debug("Adding award {} to notice {}", award.getFosId(), notice.getFosId());
                            noticesGRepo.save(notice.addAward(award));
                        } else {
                            logger.debug("Did not add {} to {} (already exists)", award.getFosId(), notice.getFosId());
                        }
                    });
                });
    }

}
