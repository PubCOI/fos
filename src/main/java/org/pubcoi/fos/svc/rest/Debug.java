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

    @GetMapping("/api/debug/add-companies")
    public void addCompanies() {
        scheduledSvc.populateFosOrgsMDBFromAwards();
    }

    @PutMapping("/api/debug/companies/all")
    public void populateCompanies() {
        scheduledSvc.populateOCCompaniesFromFosOrgs(true);
    }

    @PutMapping("/api/debug/companies/one")
    public void populateOneCompany() {
        scheduledSvc.populateOCCompaniesFromFosOrgs(false);
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
        addCompanies();
        populateCompanies();
        populateGraph();
        logger.info("Complete");
    }

    @GetMapping("/api/debug/populate-graph")
    public void populateGraph() {
        graphSvc.populateGraphFromMDB();
    }

    /**
     * calls list of companies
     * pulls back an officer
     * inserts officer and links to company
     */
    @PutMapping("/api/debug/populate-office-bearers")
    public void populateOfficeBearers() {
        OCCompanySchema companySchema = ocCompanies.findAll().stream().findFirst()
                .orElseThrow();
        companySchema.getOfficers().forEach(officer -> {
            // warning don't use in production - won't add people to two different companies
            if (!personsGraphRepo.existsByOcId(String.format("%s", officer.getOfficer().getId()))) {
                final String orgId = String.format("%s:%s", companySchema.getJurisdictionCode(), companySchema.getCompanyNumber());
                OrganisationNode org = organisationsGraphRepo
                        // first look for a version WITH persons
                        .findOrgHydratingPersons(orgId)
                        // ok, no people maybe ... so let's not hydrate
                        .orElse(organisationsGraphRepo.findOrgNotHydratingPersons(orgId)
                                // this would throw if org is not found at all
                                .orElseThrow()
                        );
                OrgPersonLink orgPersonLink = new OrgPersonLink(
                        new PersonNode(
                                String.format("%s", officer.getOfficer().getId()),
                                officer.getOfficer().getName(), officer.getOfficer().getOccupation(), officer.getOfficer().getNationality(), UUID.randomUUID().toString()
                        ), getZDT(officer.getOfficer().getStartDate()), getZDT(officer.getOfficer().getEndDate()), UUID.randomUUID().toString()
                );
                if (!org.getOrgPersons().contains(orgPersonLink)) {
                    org.getOrgPersons().add(orgPersonLink);
                    organisationsGraphRepo.save(org);
                }
            }
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
}

