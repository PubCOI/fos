package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.cdm.AttachmentFactory;
import org.pubcoi.fos.cdm.BatchJobFactory;
import org.pubcoi.fos.cdm.batch.BatchJob;
import org.pubcoi.fos.cdm.batch.BatchJobTypeEnum;
import org.pubcoi.fos.models.cf.AdditionalDetailsType;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.gdb.AwardsGraphRepo;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.gdb.NoticesGraphRepo;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.mdb.*;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.DRTaskType;
import org.pubcoi.fos.svc.models.core.FosOCCompany;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GraphSvcImpl implements GraphSvc {
    private static final Logger logger = LoggerFactory.getLogger(GraphSvcImpl.class);

    AwardsMDBRepo awardsMDBRepo;
    FosOrganisationRepo fosOrganisationRepo;
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
            FosOrganisationRepo fosOrganisationRepo,
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
        this.fosOrganisationRepo = fosOrganisationRepo;
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
        scheduledSvc.populateFosOrgsMDBFromAwards();
        scheduledSvc.populateOCCompaniesFromFosOrgs();

        // add all clients
        noticesMDBRepo.findAll().forEach(notice -> {
            Optional<ClientNode> nodeOpt = (clientsGraphRepo.findByIdEquals(ClientNode.resolveId(notice)));
            if (nodeOpt.isPresent()) {
                logger.debug("Using already instantiated client node {}", ClientNode.resolveId(notice));
            }
            ClientNode node = (nodeOpt.orElseGet(() -> {
                ClientNode clientNode = new ClientNode(notice);
                tasksSvc.createTask(new DRTask(DRTaskType.resolve_client, clientNode));
                return clientNode;
            }));
            node.addNotice(notice);
            clientsGraphRepo.save(node);
        });

        // add all awards
        awardsMDBRepo.findAll().forEach(award -> {
            if (null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FosOCCompany) {
                FosOrganisation org = award.getFosOrganisation();
                try {
                    orgGraphRepo.save(new OrganisationNode()
                            .setId(org.getId())
                            .setVerified(true)
                            .setName(ocCompaniesRepo.findById(org.getId()).orElseThrow(() -> new FosException()).getName())
                    );
                    AwardNode awardNode = new AwardNode()
                            .setId(award.getId())
                            .setValue(award.getValue())
                            .setNoticeId(award.getNoticeId())
                            .setOrganisation(
                                    orgGraphRepo.findById(org.getId()).orElseThrow(() -> new FosException()),
                                    award.getAwardedDate().toZonedDateTime(),
                                    award.getStartDate().toZonedDateTime(),
                                    award.getEndDate().toZonedDateTime()
                            );
                    logger.debug("Saving node: {}", awardNode);
                    awardsGraphRepo.save(awardNode);
                } catch (FosException e) {
                    logger.error("Unable to insert entry into graph: is source MDB fully populated?");
                }
            }
        });

        // for every notice in the mongo db, put the attachments onto the attachments DB
        noticesMDBRepo.findAll().forEach(notice -> {
            for (AdditionalDetailsType additionalDetailsType : notice.getAdditionalDetails().getAdditionalDetail()) {
                // note that each notice will have an "additional details" object that is exactly the
                // same as the description on the notice
                // helpfully, the ID and notice ID on these objects is the same
                if (additionalDetailsType.getId().equals(additionalDetailsType.getNoticeId())) continue;
                if (!attachmentMDBRepo.existsById(additionalDetailsType.getId())) {
                    attachmentMDBRepo.save(AttachmentFactory.build(additionalDetailsType));
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

        // for every award on the graph, put the associated notice
        // only adding 'verified' companies for now
        awardsGraphRepo.findAll().stream()
                .filter(award -> award.getOrganisation().isVerified())
                .forEach(award -> {
                    logger.debug("Adding notice {} to AwardNode {}", award.getNoticeId(), award.getId());
                    noticesGRepo.findById(award.getNoticeId()).ifPresent(notice -> {
                        noticesGRepo.save(notice.addAward(award));
                    });
                });
    }

}