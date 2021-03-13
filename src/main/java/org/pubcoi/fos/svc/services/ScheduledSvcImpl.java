package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.mdb.FosOrganisationRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.ReferenceTypeE;
import org.pubcoi.fos.svc.models.core.FosOCCompany;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class ScheduledSvcImpl implements ScheduledSvc {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledSvcImpl.class);

    AwardsMDBRepo awardsMDBRepo;
    OCCompaniesRepo ocCompanies;
    RestTemplate restTemplate;
    FosOrganisationRepo orgRepo;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(AwardsMDBRepo awardsMDBRepo, OCCompaniesRepo ocCompanies, RestTemplate restTemplate, FosOrganisationRepo orgRepo) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.ocCompanies = ocCompanies;
        this.restTemplate = restTemplate;
        this.orgRepo = orgRepo;
    }

    String companyRequestURL;

    @PostConstruct
    public void setup() {
        companyRequestURL = String.format("https://api.opencorporates.com/companies/gb/%%s?api_token=%s", apiToken);
    }

    /**
     * Takes all awards from the awards repo and creates FOSORG objects that will be further populated
     */
    @Override
    public void populateFosOrgsMDBFromAwards() {
        awardsMDBRepo.findAll()
                .forEach(award -> {
                    if (award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)) {
                        awardsMDBRepo.save(award.setFosOrganisation(new FosOCCompany("gb", award.getOrgReference())));
                    }
                });
    }

    /**
     * Takes companies from FOS organisations collection and populates them via calls to OpenCorporates
     */
    @Override
    public void populateOCCompaniesFromFosOrgs() {
        awardsMDBRepo.findAll()
                .forEach(award -> {
                    if (null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FosOCCompany) {
                        if (!ocCompanies.existsByCompanyNumber(((FosOCCompany) award.getFosOrganisation()).getReference())) {
                            populateFromOC(((FosOCCompany) award.getFosOrganisation()).getReference());
                        }
                    }
                });
    }

    /**
     * Performs call to OpenCorporates
     * @param companyRef the company number - assumes GB jurisdiction
     */
    void populateFromOC(String companyRef) {
        OCWrapper response = restTemplate.getForObject(String.format(companyRequestURL, companyRef), OCWrapper.class);
        if (null != response) {
            OCCompanySchema company = response.getResults().getCompany();
            ocCompanies.save(company);
            logger.info(String.format("Saved company with id %s", company.getId()));
        }
    }
}
