package org.pubcoi.fos.services;

import org.pubcoi.fos.exceptions.FOSException;
import org.pubcoi.fos.gdb.AwardsGraphRepo;
import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.gdb.NoticesGRepo;
import org.pubcoi.fos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.mdb.*;
import org.pubcoi.fos.models.core.DRTask;
import org.pubcoi.fos.models.core.DRTaskType;
import org.pubcoi.fos.models.core.FOSOCCompany;
import org.pubcoi.fos.models.core.FOSOrganisation;
import org.pubcoi.fos.models.neo.nodes.AwardNode;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.models.neo.nodes.OrganisationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GraphSvcImpl implements GraphSvc {
    private static final Logger logger = LoggerFactory.getLogger(GraphSvcImpl.class);

    AwardsMDBRepo awardsMDBRepo;
    FOSOrganisationRepo fosOrganisationRepo;
    OCCompaniesRepo ocCompaniesRepo;
    AwardsGraphRepo awardsGraphRepo;
    NoticesMDBRepo noticesMDBRepo;
    OrganisationsGraphRepo orgGraphRepo;
    ClientsGraphRepo clientsGraphRepo;
    NoticesGRepo noticesGRepo;
    ScheduledSvc scheduledSvc;
    TasksSvc tasksSvc;

    public GraphSvcImpl(AwardsMDBRepo awardsMDBRepo, AwardsGraphRepo awardsGraphRepo, FOSOrganisationRepo fosOrganisationRepo, OrganisationsGraphRepo orgGraphRepo, OCCompaniesRepo ocCompaniesRepo, NoticesMDBRepo noticesMDBRepo, ClientsGraphRepo clientsGraphRepo, NoticesGRepo noticesGRepo, ScheduledSvc scheduledSvc, TasksSvc tasksSvc) {
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
    }

    @Override
    public void clearGraphs() {
        clientsGraphRepo.deleteAll();
        awardsGraphRepo.deleteAll();
        noticesGRepo.deleteAll();
        orgGraphRepo.deleteAll();
    }

    @Override
    public void populateGraphFromMDB() {
        scheduledSvc.populateFOSOrgsMDBFromAwards();
        scheduledSvc.populateOCCompaniesFromFOSOrgs();

        // add all clients
        noticesMDBRepo.findAll().forEach(notice -> {
            Optional<ClientNode> nodeOpt = (clientsGraphRepo.findByIdEquals(ClientNode.resolveID(notice)));
            if (nodeOpt.isPresent()) {
                logger.debug("Using already instantiated client node {}", ClientNode.resolveID(notice));
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
            logger.debug("Inspecting {}:{}", award.getClass().getName(), award.getId());
            if (null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FOSOCCompany) {
                FOSOrganisation org = award.getFosOrganisation();
                try {
                    orgGraphRepo.save(new OrganisationNode()
                            .setId(org.getId())
                            .setVerified(true)
                            .setCompanyName(ocCompaniesRepo.findById(org.getId()).orElseThrow(() -> new FOSException()).getName())
                    );
                    awardsGraphRepo.save(new AwardNode()
                            .setId(award.getId())
                            .setValue(award.getValue())
                            .setNoticeID(award.getNoticeID())
                            .setOrganisation(
                                    orgGraphRepo.findById(org.getId()).orElseThrow(() -> new FOSException()),
                                    award.getAwardedDate().toZonedDateTime(),
                                    award.getStartDate().toZonedDateTime(),
                                    award.getEndDate().toZonedDateTime()
                            )
                    );
                    logger.debug("Saved {}:{}", award.getClass().getName(), award.getId());
                } catch (FOSException e) {
                    logger.error("Unable to insert entry into graph: is source MDB fully populated?");
                }
            }
        });

        // only adding 'verified' companies for now
        awardsGraphRepo.findAll().stream()
                .filter(award -> award.getOrganisation().isVerified())
                .forEach(award -> {
                    logger.debug("Adding notice ID {} to {}:{}", award.getNoticeID(), award.getClass().getName(), award.getId());
                    noticesGRepo.findById(award.getNoticeID()).ifPresent(notice -> {
                        noticesGRepo.save(notice.addAward(award));
                    });
                });
    }

}
