package org.pubcoi.fos.svc.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.gdb.ClientNodeFTS;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.gdb.PersonsGraphRepo;
import org.pubcoi.fos.svc.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.neo.relationships.OrgPersonLink;
import org.pubcoi.fos.svc.services.GraphSvc;
import org.pubcoi.fos.svc.services.ScheduledSvc;
import org.pubcoi.fos.svc.services.TransactionOrchestrationSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Profile("debug")
@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final GraphSvc graphSvc;
    final ScheduledSvc scheduledSvc;
    final ClientNodeFTS clientNodeFTS;
    final TransactionOrchestrationSvc transactionOrchestrationSvc;
    final TasksRepo tasksRepo;
    final ClientsGraphRepo clientsGraphRepo;
    final PersonsGraphRepo personsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;

    public Debug(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            OCCompaniesRepo ocCompanies,
            GraphSvc graphSvc,
            ScheduledSvc scheduledSvc,
            ClientNodeFTS clientNodeFTS,
            TransactionOrchestrationSvc transactionOrchestrationSvc,
            TasksRepo tasksRepo,
            ClientsGraphRepo clientsGraphRepo, PersonsGraphRepo personsGraphRepo, OrganisationsGraphRepo organisationsGraphRepo) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphSvc = graphSvc;
        this.scheduledSvc = scheduledSvc;
        this.clientNodeFTS = clientNodeFTS;
        this.transactionOrchestrationSvc = transactionOrchestrationSvc;
        this.tasksRepo = tasksRepo;
        this.clientsGraphRepo = clientsGraphRepo;
        this.personsGraphRepo = personsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
    }

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return ocCompanies.findAll();
    }

    @GetMapping("/api/debug/populate-companies")
    public void populateCompanies() {
        scheduledSvc.populateFosOrgsMDBFromAwards();
    }

    @DeleteMapping("/api/debug/clear-graphs")
    public void clearGraphs() {
        graphSvc.clearGraphs();
    }

    @DeleteMapping("/api/debug/clear-mdb")
    public void clearMDB() {
        transactionOrchestrationSvc.clearTransactions();
        tasksRepo.deleteAll();
    }

    @DeleteMapping("/api/debug/clear-all")
    public void clearAll() {
        clearGraphs();
        clearMDB();
    }

    @PutMapping("/api/debug/populate-all")
    public void populateAll() {
        populateCompanies();
        populateGraph();
        logger.info("Complete");
    }

    @GetMapping("/api/debug/populate-graph")
    public void populateGraph() {
        graphSvc.populateGraphFromMDB();
    }

    /**
     * DEBUG CLASS
     * <p>
     * calls list of companies
     * pulls back an officer
     * inserts officer and links to company
     */
    @PutMapping("/api/debug/populate-office-bearers")
    public void populateOfficeBearers() {
        organisationsGraphRepo.findAll().forEach(organisationNode -> {
            if (null == organisationNode.getReference() || null == organisationNode.getJurisdiction()) {
                logger.info("{} not an OC node, skipping", organisationNode);
                return;
            }
            OCCompanySchema companySchema = ocCompanies.findByCompanyNumberAndJurisdictionCode(organisationNode.getReference(), organisationNode.getJurisdiction());
            companySchema.getOfficers().forEach(officer -> {
                logger.debug("Looking up details for officer {}", getUID(officer.getOfficer().getId()));
                // warning don't use in production - won't add people to two different companies
                // if (!personsGraphRepo.existsByOcId(getUID(officer.getOfficer().getId()))) { // todo add getid to aspects
                final String orgId = String.format("%s:%s", companySchema.getJurisdictionCode(), companySchema.getCompanyNumber());
                logger.debug("Looking up org details for {}", orgId);

                // first try find org using stated ID
                Optional<OrganisationNode> orgOpt = organisationsGraphRepo
                        // first look for a version WITH persons
                        .findOrgHydratingPersons(orgId);

                if (orgOpt.isEmpty()) {
                    orgOpt = organisationsGraphRepo.findOrgNotHydratingPersons(orgId);
                }

                // null
                OrganisationNode org = orgOpt.get();

                OrgPersonLink orgPersonLink = new OrgPersonLink(
                        new PersonNode(
                                getUID(officer.getOfficer().getId()),
                                officer.getOfficer().getName(),
                                officer.getOfficer().getOccupation(),
                                officer.getOfficer().getNationality(),
                                UUID.randomUUID().toString()
                        ),
                        org.getId(),
                        officer.getOfficer().getPosition(),
                        getZDT(officer.getOfficer().getStartDate()),
                        getZDT(officer.getOfficer().getEndDate()),
                        UUID.randomUUID().toString()
                );
                if (!org.getOrgPersons().contains(orgPersonLink)) {
                    logger.debug("org {} does not contain person {}: adding with link ID {}", org.getId(), orgPersonLink.getPerson().getId(), orgPersonLink.getId());
                    org.getOrgPersons().add(orgPersonLink);
                    organisationsGraphRepo.save(org);
                }
            });
        });
    }

    private LocalDate getDate(Object date) {
        if (null == date) {
            return null;
        }
        if (date instanceof String) {
            return LocalDate.parse((CharSequence) date);
        }
        throw new IllegalArgumentException("Must provide valid date string");
    }

    private ZonedDateTime getZDT(Object date) {
        LocalDate localDate = getDate(date);
        if (null == localDate) {
            return null;
        }
        return localDate.atStartOfDay(ZoneOffset.UTC);
    }

    private String getUID(Double id) {
        return String.format("%.0f", id);
    }
}

