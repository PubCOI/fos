package org.pubcoi.fos.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.cf.ReferenceTypeE;
import org.pubcoi.fos.services.GraphsOrchestration;
import org.pubcoi.fos.services.OperationsSvc;
import org.pubcoi.fos.services.ScheduledSvc;
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
    ScheduledSvc scheduledSvc;
    OCCompaniesRepo companiesRepo;
    GraphsOrchestration graphsOrchestration;
    OperationsSvc operationsSvc;

    public Debug(NoticesMDBRepo noticesMDBRepo, ScheduledSvc scheduledSvc, OCCompaniesRepo companiesRepo, GraphsOrchestration graphsOrchestration, OperationsSvc operationsSvc) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.scheduledSvc = scheduledSvc;
        this.companiesRepo = companiesRepo;
        this.graphsOrchestration = graphsOrchestration;
        this.operationsSvc = operationsSvc;
    }

    @GetMapping(value = "/api/notices")
    public List<FullNotice> getNotices() {
        return noticesMDBRepo.findAll();
    }

    @PutMapping(value = "/api/notices", consumes = {MediaType.APPLICATION_XML_VALUE})
    public String putNotices(@RequestBody ArrayOfFullNotice array) {
        for (FullNotice fullNotice : array.getFullNotice()) {
            operationsSvc.saveNotice(fullNotice);
        }
        return "ok";
    }

    @GetMapping(value = "/api/company-insert")
    public String updateCompany() {
        Optional<FullNotice> notice = noticesMDBRepo.findAll().stream()
                .filter(a -> a.getAwards().getAwardDetail().stream()
                        .filter(b -> !companiesRepo.existsByCompanyNumber(b.getReference()))
                        .anyMatch(c -> c.getReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE))
                ).findFirst();
        if (!notice.isPresent()) {
            return "company not found";
        }
        scheduledSvc.insertOrUpdateAwardCompany(notice.get());
        return "ok";
    }

    @GetMapping(value = "/api/company-populate")
    public String populateOne() {
        scheduledSvc.populateOne();
        return "ok";
    }

    @GetMapping(value = "/api/graph/populate-company")
    public String populateGraphCompany() {
        graphsOrchestration.populateOne();
        return "ok";
    }

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return companiesRepo.findAll();
    }

    @DeleteMapping("/api/awards")
    public String deleteAwards() {
        graphsOrchestration.deleteAll();
        return "ok";
    }

    @PostMapping("/api/awards")
    public String createRelationship() {
        graphsOrchestration.createOneRelationship();
        return "ok";
    }
}
