package org.pubcoi.fos.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.gdb.ClientNodeFTS;
import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.mdb.TasksRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    NoticesMDBRepo noticesMDBRepo;
    OCCompaniesRepo ocCompanies;
    GraphsOrchestration graphsOrch;
    GraphSvc graphSvc;
    OperationsSvc operations;
    ScheduledSvc scheduledSvc;
    ClientNodeFTS clientNodeFTS;
    TransactionSvc transactionSvc;
    TasksRepo tasksRepo;
    ClientsGraphRepo clientsGraphRepo;

    public Debug(
            NoticesMDBRepo noticesMDBRepo,
            OCCompaniesRepo ocCompanies,
            GraphsOrchestration graphsOrch,
            GraphSvc graphSvc,
            OperationsSvc operations,
            ScheduledSvc scheduledSvc,
            ClientNodeFTS clientNodeFTS,
            TransactionSvc transactionSvc,
            TasksRepo tasksRepo,
            ClientsGraphRepo clientsGraphRepo) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphsOrch = graphsOrch;
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

    @DeleteMapping("/api/awards")
    public String deleteAwards() {
        graphsOrch.deleteAll();
        return "ok";
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
    public Optional<ClientNode> getClients(@PathVariable("client") String client) {
        logger.info("Requesting client {} and neighbours", client);
        return clientsGraphRepo.findByIdEqualsIncludeNeighbours(client);
    }

    @GetMapping("/api/debug/clients/a/{client}")
    public Optional<ClientNode> getClient2(@PathVariable("client") String client) {
        logger.info("getClient2 w/client {}", client);
        return clientsGraphRepo.findByClientIdIncludeNeighboursIgnoringHidden(client);
    }
}
