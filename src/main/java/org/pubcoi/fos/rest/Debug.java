package org.pubcoi.fos.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.gdb.ClientNodeFTS;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.dao.ClientNodeFTSDAOResponse;
import org.pubcoi.fos.services.GraphSvc;
import org.pubcoi.fos.services.GraphsOrchestration;
import org.pubcoi.fos.services.OperationsSvc;
import org.pubcoi.fos.services.ScheduledSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    public Debug(NoticesMDBRepo noticesMDBRepo, OCCompaniesRepo ocCompanies, GraphsOrchestration graphsOrch, GraphSvc graphSvc, OperationsSvc operations, ScheduledSvc scheduledSvc, ClientNodeFTS clientNodeFTS) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphsOrch = graphsOrch;
        this.graphSvc = graphSvc;
        this.operations = operations;
        this.scheduledSvc = scheduledSvc;
        this.clientNodeFTS = clientNodeFTS;
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

    @GetMapping("/api/debug/populate-graph")
    public void populateGraph() {
        graphSvc.populateGraphFromMDB();
    }

    @GetMapping("/api/debug/search/{query}")
    public List<ClientNodeFTSDAOResponse> runQuery(@PathVariable String query) {
        return clientNodeFTS.findAllDTOProjectionsWithCustomQuery(query)
                .stream()
                .limit(5)
                .map(ClientNodeFTSDAOResponse::new)
                .collect(Collectors.toList());
    }
}
