package org.pubcoi.fos.rest;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.dao.NoticesRepo;
import org.pubcoi.fos.dao.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.cf.ReferenceTypeE;
import org.pubcoi.fos.services.ScheduledSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    NoticesRepo noticesRepo;
    ScheduledSvc scheduledSvc;
    OCCompaniesRepo companiesRepo;

    public Debug(NoticesRepo noticesRepo, ScheduledSvc scheduledSvc, OCCompaniesRepo companiesRepo) {
        this.noticesRepo = noticesRepo;
        this.scheduledSvc = scheduledSvc;
        this.companiesRepo = companiesRepo;
    }

    @GetMapping(value = "/api/notices")
    public List<FullNotice> getNotices() {
        return noticesRepo.findAll();
    }

    @PutMapping(value = "/api/notices", consumes = {MediaType.APPLICATION_XML_VALUE})
    public String putNotices(@RequestBody ArrayOfFullNotice array) {
        for (FullNotice fullNotice : array.getFullNotice()) {
            logger.info("Inserting: notice " + fullNotice.getId());
            noticesRepo.save(fullNotice);
        }
        return "ok";
    }

    @GetMapping(value = "/api/company-insert")
    public String updateCompany() {
        Optional<FullNotice> notice = noticesRepo.findAll().stream()
                .filter(a -> a.getAwards().getAwardDetail().stream()
                        .anyMatch(b -> b.getReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE))).findFirst();
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

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return companiesRepo.findAll();
    }
}
