package org.pubcoi.fos.svc.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.gdb.ClientNodeFTS;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
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

import java.util.List;

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

    public Debug(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            OCCompaniesRepo ocCompanies,
            GraphSvc graphSvc,
            ScheduledSvc scheduledSvc,
            ClientNodeFTS clientNodeFTS,
            TransactionOrchestrationSvc transactionOrchestrationSvc,
            TasksRepo tasksRepo,
            ClientsGraphRepo clientsGraphRepo) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphSvc = graphSvc;
        this.scheduledSvc = scheduledSvc;
        this.clientNodeFTS = clientNodeFTS;
        this.transactionOrchestrationSvc = transactionOrchestrationSvc;
        this.tasksRepo = tasksRepo;
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return ocCompanies.findAll();
    }

    @GetMapping("/api/debug/add-companies")
    public void addCompanies() {
        scheduledSvc.populateFosOrgsMDBFromAwards();
    }

    @GetMapping("/api/debug/populate-companies")
    public void populateCompanies() {
        scheduledSvc.populateOCCompaniesFromFosOrgs();
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
}
