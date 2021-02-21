package org.pubcoi.fos.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.FOSOrganisationRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.cf.ReferenceTypeE;
import org.pubcoi.fos.models.core.FOSOCCompany;
import org.pubcoi.fos.models.oc.OCWrapper;
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
    FOSOrganisationRepo orgRepo;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(AwardsMDBRepo awardsMDBRepo, OCCompaniesRepo ocCompanies, RestTemplate restTemplate, FOSOrganisationRepo orgRepo) {
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
    public void populateFOSOrgsMDBFromAwards() {
        awardsMDBRepo.findAll()
                .forEach(award -> {
                    if (award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)) {
                        awardsMDBRepo.save(award.setFosOrganisation(new FOSOCCompany("gb", award.getOrgReference())));
                    }
                });
    }

    /**
     * Takes companies from FOS organisations collection and populates them via calls to OpenCorporates
     */
    @Override
    public void populateOCCompaniesFromFOSOrgs() {
        awardsMDBRepo.findAll()
                .forEach(award -> {
                    if (null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FOSOCCompany) {
                        if (!ocCompanies.existsByCompanyNumber(((FOSOCCompany) award.getFosOrganisation()).getReference())) {
                            populateFromOC(((FOSOCCompany) award.getFosOrganisation()).getReference());
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
            logger.info(String.format("Saved company with ID %s", company.getId()));
        }
    }
}
