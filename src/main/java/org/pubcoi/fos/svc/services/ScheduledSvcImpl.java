package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import info.debatty.java.stringsimilarity.NGram;
import org.pubcoi.cdm.cf.ReferenceTypeE;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.mdb.OrganisationsMDBRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.core.*;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledSvcImpl implements ScheduledSvc {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledSvcImpl.class);

    final AwardsMDBRepo awardsMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final OCRestSvc ocRestSvc;
    final RestTemplate restTemplate;
    final OrganisationsMDBRepo orgMDBRepo;
    final TasksRepo tasksRepo;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            OCCompaniesRepo ocCompanies,
            OCRestSvc ocRestSvc, RestTemplate restTemplate,
            OrganisationsMDBRepo orgMDBRepo,
            OrganisationsGraphRepo orgGraphRepo,
            TasksRepo tasksRepo
    ) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.ocCompanies = ocCompanies;
        this.ocRestSvc = ocRestSvc;
        this.restTemplate = restTemplate;
        this.orgMDBRepo = orgMDBRepo;
        this.tasksRepo = tasksRepo;
    }

    /**
     * Takes all awards from the awards repo and creates FOSORG objects that will be further populated
     */
    @Override
    public void populateFosOrgsMDBFromAwards() {
        awardsMDBRepo.findAll()
                .forEach(award -> {

                    // as it turns out ... contracts finder doesn't always have the correct company number :(
                    // so we have to do a sense check ...
                    OCCompanySchema companySchema = (null != award.getOrgReferenceType() && award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)) ?
                            getCompany(award.getOrgReference(), JurisdictionEnum.gb) : null;

                    FosOrganisation org = (null == companySchema) ? new FosNonCanonicalOrg(award) :
                            new FosCanonicalOrg(JurisdictionEnum.gb.toString(), companySchema.getCompanyNumber());

                    if (org instanceof FosCanonicalOrg) {
                        // of course, companySchema is implicitly not null
                        if (isSimilar(award.getSupplierName(), companySchema.getName())) {
                            org.setVerified(true);
                        } else {
                            logger.warn(
                                    "Supplier name {} does not match company name {} in Companies House",
                                    award.getSupplierName(), companySchema.getName()
                            );
                        }
                    }

                    // save new org if it doesn't exist
                    if (!orgMDBRepo.existsById(org.getId())) {
                        if (!(org instanceof FosCanonicalOrg) || !org.getVerified()) {
                            logger.debug(
                                    "Generating {} task for {}", FosTaskType.resolve_company, org
                            );
                            tasksRepo.save(new DRTask(FosTaskType.resolve_company, org));
                        }
                        orgMDBRepo.save(org);
                    }

                    // save award with org
                    awardsMDBRepo.save(award.setFosOrganisation(org));
                });
    }

    /**
     * Gets a company locally, or looks it up on OpenCorporates if it's not found (via a cached search)
     *
     * @param companyReference the company number
     * @param jurisdiction     at present, only GB
     * @return {@link OCCompanySchema} if the company exists, otherwise null
     */
    OCCompanySchema getCompany(String companyReference, JurisdictionEnum jurisdiction) {
        // see if the company exists on our DB first
        OCCompanySchema mdbCompany = ocCompanies.findByCompanyNumberAndJurisdictionCode(companyReference, jurisdiction.toString());
        if (null != mdbCompany) return mdbCompany;

        // then see if it exists on OC
        OCWrapper companyLookup = ocRestSvc.getCompany(companyReference, jurisdiction);
        if (null != companyLookup.getResults() && null != companyLookup.getResults().getCompany()) {
            ocCompanies.save(companyLookup.getResults().getCompany());
        }
        return (null != companyLookup.getResults()) ? companyLookup.getResults().getCompany() : null;
    }

    boolean isSimilar(String in1, String in2) {
        NGram nGram = new NGram(4);
        String str1 = in1.toLowerCase();
        String str2 = in2.toLowerCase();
        double similarity = 1 - nGram.distance(str1, str2);
        logger.debug("Similarity of '{}' and '{}' is reported as {}", str1, str2, similarity);
        return similarity > 0.90;
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
            } else {
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
        OCWrapper response = ocRestSvc.getCompany(companyRef, JurisdictionEnum.gb);
        if (null != response) {
            OCCompanySchema company = response.getResults().getCompany();
            ocCompanies.save(company);
            logger.info(String.format("Saved company with ID %s", company.getId()));
        }
    }
}
