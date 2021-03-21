package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.cdm.cf.ReferenceTypeE;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.mdb.OrganisationsMDBRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.core.*;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledSvcImpl implements ScheduledSvc {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledSvcImpl.class);

    final AwardsMDBRepo awardsMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final RestTemplate restTemplate;
    final OrganisationsMDBRepo orgMDBRepo;
    final TasksRepo tasksRepo;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            OCCompaniesRepo ocCompanies,
            RestTemplate restTemplate,
            OrganisationsMDBRepo orgMDBRepo,
            OrganisationsGraphRepo orgGraphRepo,
            TasksRepo tasksRepo
    ) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.ocCompanies = ocCompanies;
        this.restTemplate = restTemplate;
        this.orgMDBRepo = orgMDBRepo;
        this.tasksRepo = tasksRepo;
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
                    FosOrganisation organisation = (award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)) ?
                            new FosCanonicalOrg("gb", award.getOrgReference()) :
                            new FosNonCanonicalOrg(award);

                    // save new org if it doesn't exist
                    if (!orgMDBRepo.existsById(organisation.getId())) {
                        if (!(organisation instanceof FosCanonicalOrg)) {
                            logger.debug("Not seen this org before: Generating {} task for {}", FosTaskType.resolve_company, organisation);
                            tasksRepo.save(new DRTask(FosTaskType.resolve_company, organisation));
                        }
                        orgMDBRepo.save(organisation);
                    }

                    // save award with org
                    awardsMDBRepo.save(award.setFosOrganisation(organisation));
                });
    }

    /**
     * Takes companies from FOS organisations collection and populates them via calls to OpenCorporates
     */
    @Override
    public void populateOCCompaniesFromFosOrgs(boolean all) {
        List<CFAward> awards = awardsMDBRepo.findAll().stream()
                .filter(award -> null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FosCanonicalOrg)
                .filter(award -> !ocCompanies.existsByCompanyNumber(((FosCanonicalOrg) award.getFosOrganisation()).getReference())).collect(Collectors.toList());

        if (awards.size() > 0) {
            logger.debug("{} companies left to populate", awards.size());
            if (all) {
                awards.forEach(award -> {
                    populateFromOC(((FosCanonicalOrg) award.getFosOrganisation()).getReference());
                });
            }
            else {
                CFAward award = awards.stream().findAny().orElseThrow();
                populateFromOC(((FosCanonicalOrg) award.getFosOrganisation()).getReference());
            }
        }
    }

    /**
     * Performs call to OpenCorporates
     *
     * @param companyRef the company number - assumes GB jurisdiction
     */
    void populateFromOC(String companyRef) {
        logger.debug("Populating data for {}", companyRef);
        OCWrapper response = restTemplate.getForObject(String.format(companyRequestURL, companyRef), OCWrapper.class);
        if (null != response) {
            OCCompanySchema company = response.getResults().getCompany();
            ocCompanies.save(company);
            logger.info(String.format("Saved company with ID %s", company.getId()));
        }
    }
}
