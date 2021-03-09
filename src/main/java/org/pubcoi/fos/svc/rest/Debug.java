package org.pubcoi.fos.svc.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.gdb.ClientNodeFTS;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Profile("debug")
@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    final BatchExecutorSvc batchExecutorSvc;
    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final GraphSvc graphSvc;
    final OperationsSvc operations;
    final ScheduledSvc scheduledSvc;
    final ClientNodeFTS clientNodeFTS;
    final TransactionSvc transactionSvc;
    final TasksRepo tasksRepo;
    final ClientsGraphRepo clientsGraphRepo;

    public Debug(
            BatchExecutorSvc batchExecutorSvc,
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            OCCompaniesRepo ocCompanies,
            GraphSvc graphSvc,
            OperationsSvc operations,
            ScheduledSvc scheduledSvc,
            ClientNodeFTS clientNodeFTS,
            TransactionSvc transactionSvc,
            TasksRepo tasksRepo,
            ClientsGraphRepo clientsGraphRepo) {
        this.batchExecutorSvc = batchExecutorSvc;
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphSvc = graphSvc;
        this.operations = operations;
        this.scheduledSvc = scheduledSvc;
        this.clientNodeFTS = clientNodeFTS;
        this.transactionSvc = transactionSvc;
        this.tasksRepo = tasksRepo;
        this.clientsGraphRepo = clientsGraphRepo;
    }

    @GetMapping(value = "/api/notices")
    public List<FullNotice> getNotices() {
        return noticesMDBRepo.findAll();
    }

    @PutMapping(value = "/api/notices", consumes = {MediaType.APPLICATION_XML_VALUE})
    public String putNotices(@RequestBody ArrayOfFullNotice array) {
        for (FullNotice fullNotice : array.getFullNotice()) {
            operations.saveNotice(fullNotice);
        }
        return "ok";
    }

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return ocCompanies.findAll();
    }

    @GetMapping("/api/debug/add-companies")
    public void addCompanies() {
        scheduledSvc.populateFOSOrgsMDBFromAwards();
    }

    @GetMapping("/api/debug/populate-companies")
    public void populateCompanies() {
        scheduledSvc.populateOCCompaniesFromFOSOrgs();
    }

    @DeleteMapping("/api/debug/clear-graphs")
    public void clearGraphs() {
        graphSvc.clearGraphs();
    }

    @DeleteMapping("/api/debug/clear-mdb")
    public void clearMDB() {
        transactionSvc.clearTransactions();
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

    @GetMapping("/api/debug/clients/{client}")
    public Optional<ClientNode> getClient2(@PathVariable("client") String client) {
        logger.info("getClient w/tenders {}", client);
        return clientsGraphRepo.findClientsHydratingTenders(client);
    }

    @GetMapping("/api/debug/add-jobs")
    public void addBatchJobs() {
        attachmentMDBRepo.findAll().stream()
                .filter(a -> a.getS3Locations().isEmpty() && a.getDataType().equals("Attachment"))
                .limit(25)
                .forEach(a -> {
                    try {
                        batchExecutorSvc.runBatch(a);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }
}
