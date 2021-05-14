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
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.fos.AttachmentFactory;
import org.pubcoi.cdm.fos.BatchJobFactory;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.core.FosTaskType;
import org.pubcoi.fos.svc.models.neo.nodes.*;
import org.pubcoi.fos.svc.models.neo.relationships.AwardOrgLink;
import org.pubcoi.fos.svc.models.neo.relationships.OrgPersonLink;
import org.pubcoi.fos.svc.repos.gdb.jpa.*;
import org.pubcoi.fos.svc.repos.mdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.pubcoi.fos.svc.services.Utils.getZDT;

@Service
public class GraphSvcImpl implements GraphSvc {
    private static final Logger logger = LoggerFactory.getLogger(GraphSvcImpl.class);

    final AwardsMDBRepo awardsMDBRepo;
    final OrganisationsMDBRepo organisationsMDBRepo;
    final OCCompaniesRepo ocCompaniesRepo;
    final AwardsGraphRepo awardsGraphRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final OrganisationsGraphRepo orgGraphRepo;
    final ClientsGraphRepo clientsGraphRepo;
    final NoticesGraphRepo noticesGraphRepo;
    final PersonsGraphRepo personsGraphRepo;
    final ScheduledSvc scheduledSvc;
    final TasksSvc tasksSvc;
    final AttachmentMDBRepo attachmentMDBRepo;
    final BatchJobMDBRepo batchJobMDBRepo;

    public GraphSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            AwardsGraphRepo awardsGraphRepo,
            OrganisationsMDBRepo organisationsMDBRepo,
            OrganisationsGraphRepo orgGraphRepo,
            OCCompaniesRepo ocCompaniesRepo,
            NoticesMDBRepo noticesMDBRepo,
            ClientsGraphRepo clientsGraphRepo,
            NoticesGraphRepo noticesGraphRepo,
            PersonsGraphRepo personsGraphRepo,
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
        this.noticesGraphRepo = noticesGraphRepo;
        this.personsGraphRepo = personsGraphRepo;
        this.scheduledSvc = scheduledSvc;
        this.tasksSvc = tasksSvc;
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.batchJobMDBRepo = batchJobMDBRepo;
    }

    private void addNoticeToGraph(FullNotice notice) {
        Optional<ClientNode> nodeOpt = (clientsGraphRepo.findClientHydratingNotices(ClientNode.resolveId(notice)));
        if (nodeOpt.isPresent()) {
            logger.debug("Using already instantiated client node {}", ClientNode.resolveId(notice));
        }
        ClientNode client = (nodeOpt.orElseGet(() -> {
            ClientNode clientNode = new ClientNode(notice);
            tasksSvc.createTask(new DRTask(FosTaskType.resolve_client, clientNode));
            return clientNode;
        }));
        NoticeNode noticeNode = (noticesGraphRepo.findByFosId(notice.getId()).orElse(new NoticeNode(notice)));
        if (!clientsGraphRepo.relationshipExists(client.getFosId(), noticeNode.getFosId())) {
            client.addNotice(noticeNode, notice.getId(), notice.getNotice().getPublishedDate().toZonedDateTime());
        }
        clientsGraphRepo.save(client);
    }

    private void addAwardToGraph(CFAward award) {
        if (null != award.getFosOrganisation()) {
            FosOrganisation org = award.getFosOrganisation();
            try {
                logger.trace("Looking up OrganisationNode {}", org.getFosId());
                final OrganisationNode orgNode = orgGraphRepo.findByFosId(org.getFosId()).orElseGet(() -> {
                            logger.trace(Ansi.Yellow.format("Did not find OrganisationNode %s: instantiating new instance", org.getFosId()));
                            return new OrganisationNode(org);
                        }
                );

                logger.trace("Looking up AwardNode {}", award.getId());
                AwardNode awardNode = awardsGraphRepo.findByFosId(award.getId()).orElseGet(() -> {
                            logger.trace(Ansi.Yellow.format("Did not find AwardNode %s: instantiating new instance", award.getId()));
                            return new AwardNode()
                                    .setFosId(award.getId())
                                    .setValue(award.getValue())
                                    .setNoticeId(award.getNoticeId())
                                    .setGroupAward(award.getGroup());
                        }
                );

                // check if award<->org exists ... if not create it
                if (!awardsGraphRepo.relationshipExists(awardNode.getFosId(), orgNode.getFosId())) {
                    logger.trace(Ansi.Yellow.format("Relationship between AwardNode %s and OrganisationNode %s does not exist: linking nodes", awardNode.getFosId(), orgNode.getFosId()));
                    AwardOrgLink awLink = new AwardOrgLink(orgNode, award.getAwardedDate().toLocalDate(), award.getStartDate().toLocalDate(), award.getEndDate().toLocalDate());
                    awardNode.addAwardee(awLink);
                    logger.debug("Saved {}", awLink);
                }

                orgGraphRepo.save(orgNode);
                awardsGraphRepo.save(awardNode);

                logger.trace("Attempting to add backwards ref for notice {} to AwardNode {}", awardNode.getNoticeId(), awardNode.getFosId());
                noticesGraphRepo.findByFosId(awardNode.getNoticeId()).ifPresent(notice -> {
                    logger.trace("Found notice {}", notice);
                    if (!noticesGraphRepo.isLinkedToAward(notice.getFosId(), awardNode.getFosId())) {
                        logger.debug(Ansi.Yellow.format("Relationship between NoticeNode %s and AwardNode %s does not exist: linking nodes", notice.getFosId(), awardNode.getFosId()));
                        noticesGraphRepo.save(notice.addAward(awardNode));
                    } else {
                        logger.debug("Did not add {} to {} (already exists)", awardNode.getFosId(), notice.getFosId());
                    }
                });

                logger.debug("Saved {}", awardNode);
            } catch (FosEndpointException e) {
                logger.error(Ansi.Red.colorize("Unable to insert entry into graph: is source MDB fully populated?"), e);
            }
        } else {
            logger.debug("Unable to find OC entry for {}", award.getSupplierName());
        }
    }

    /**
     * This is just a convenience function for now ...
     */
    @Override
    public void populateGraphFromMDB() {
        logger.info("Populating organisations cache");
        scheduledSvc.populateFosOrgsMDBFromAwards();
        logger.info("Adding all notices to graph");
        noticesMDBRepo.findAll().forEach(this::addNoticeToGraph);
        logger.info("Adding all awards to graph");
        awardsMDBRepo.findAll().forEach(this::addAwardToGraph);
        logger.info("Adding all office bearers");
        orgGraphRepo.findAllNotHydrating().forEach(this::populateOfficeBearers);
    }

    @Override
    public void populateGraphFromMDB(String noticeId) {
        // overhead is near-zero, might as well do this initial step without filtering
        scheduledSvc.populateFosOrgsMDBFromAwards();
        noticesMDBRepo.findById(noticeId).ifPresent(this::addNoticeToGraph);
        awardsMDBRepo.findAllByNoticeId(noticeId).forEach(this::addAwardToGraph);
        noticesGraphRepo.findByFosId(noticeId).ifPresent(n -> {
            n.getAwards().forEach(award -> {
                award.getAwardees().forEach(awardee -> {
                    populateOfficeBearers(awardee.getOrganisationNode());
                });
            });
        });
    }

    private void populateOfficeBearers(OrganisationNode orgNode) {
        if (null == orgNode.getReference() || null == orgNode.getJurisdiction()) {
            logger.info("{} not an OC node, skipping", orgNode);
            return;
        }
        OCCompanySchema companySchema = ocCompaniesRepo.findByCompanyNumberAndJurisdictionCode(orgNode.getReference(), orgNode.getJurisdiction());
        companySchema.getOfficers().forEach(officer -> {
            logger.debug("Looking up details for officer {} (fosId:{})",
                    PersonNode.convertPersonOCIdToString(officer.getOfficer().getId()),
                    PersonNode.generatePersonId(officer.getOfficer())
            );
            // warning don't use in production - won't add people to two different companies
            // if (!personsGraphRepo.existsByOcId(getUID(officer.getOfficer().getId()))) { // todo add getid to aspects

            final String transactionId = UUID.randomUUID().toString();
            final String personNodeId = PersonNode.generatePersonId(officer.getOfficer());
            PersonNode personNode = personsGraphRepo
                    .findByFosId(personNodeId)
                    .orElseGet(() -> {
                        logger.debug(Ansi.Yellow.format("Did not find PersonNode %s: instantiating new instance", personNodeId));
                        return new PersonNode(PersonNodeType.OfficeBearer, officer.getOfficer(), transactionId);
                    });

            if (!orgGraphRepo.relationshipExists(orgNode.getFosId(), personNode.getFosId())) {
                logger.debug(Ansi.Yellow.format("Relationship between OrganisationNode %s and PersonNode %s does not exist: linking nodes", orgNode.getFosId(), personNode.getFosId()));
                OrgPersonLink orgPersonLink = new OrgPersonLink(personNode,
                        orgNode.getFosId(),
                        officer.getOfficer().getPosition(),
                        getZDT(officer.getOfficer().getStartDate()),
                        getZDT(officer.getOfficer().getEndDate()), transactionId);
                orgNode.addPerson(orgPersonLink);
                logger.trace("Completed adding person {} to organisation {}", personNode.getFosId(), orgNode.getFosId());
            }
            orgGraphRepo.save(orgNode);
        });
    }

    @Override
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
