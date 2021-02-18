package org.pubcoi.fos.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.mdb.FOSCompaniesRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.core.FOSCompany;
import org.pubcoi.fos.models.core.DataSources;
import org.pubcoi.fos.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.pubcoi.fos.models.cf.ReferenceTypeE.COMPANIES_HOUSE;

@Service
public class ScheduledSvcImpl implements ScheduledSvc {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledSvcImpl.class);

    FOSCompaniesRepo fosCompaniesRepo;
    OCCompaniesRepo ocCompaniesRepo;
    RestTemplate restTemplate;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(FOSCompaniesRepo fosCompaniesRepo, OCCompaniesRepo ocCompaniesRepo, RestTemplate restTemplate) {
        this.fosCompaniesRepo = fosCompaniesRepo;
        this.ocCompaniesRepo = ocCompaniesRepo;
        this.restTemplate = restTemplate;
    }

    String companyRequestURL;

    @PostConstruct
    public void setup() {
        companyRequestURL = String.format("https://api.opencorporates.com/companies/gb/%%s?api_token=%s", apiToken);
    }

    @Override
    public void insertOrUpdateAwardCompany(FullNotice notice) {
        for (AwardDetailParentType.AwardDetail award : notice.getAwards().getAwardDetail()) {
            if (award.getReferenceType().equals(COMPANIES_HOUSE)) {
                if (!fosCompaniesRepo.existsBySourceAndReference(DataSources.oc_company, award.getReference())) {
                    fosCompaniesRepo.save(new FOSCompany(award));
                }
            }
        }
    }

    @Override
    public void populateOne() {
        Optional<FOSCompany> c = fosCompaniesRepo.findAll().stream()
                .filter(d -> d.getSource().equals(DataSources.oc_company))
                .filter(e -> !ocCompaniesRepo.existsByCompanyNumber(e.getReference()))
                .findFirst();
        if (!c.isPresent()) {
            logger.info("Ran populate, no companies found");
            return;
        }

        OCWrapper response = restTemplate.getForObject(String.format(companyRequestURL, c.get().getReference()), OCWrapper.class);
        if (null != response) {
            OCCompanySchema company = response.getResults().getCompany();
            ocCompaniesRepo.save(company);
            logger.info(String.format("Saved company with ID %s", company.getId()));
        }
    }
}
