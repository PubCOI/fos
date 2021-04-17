package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import info.debatty.java.stringsimilarity.NGram;
import org.pubcoi.cdm.cf.ReferenceTypeE;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
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

import java.util.regex.Matcher;

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
                    OCCompanySchema ocCompany = (null != award.getOrgReferenceType() && award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)) ?
                            getCompany(award.getOrgReference(), JurisdictionEnum.gb) : null;

                    FosOrganisation org = (null == ocCompany) ? new FosNonCanonicalOrg(award) :
                            new FosCanonicalOrg(ocCompany);

                    // save new org if it doesn't exist
                    if (!orgMDBRepo.existsById(org.getId())) {
                        if (org instanceof FosCanonicalOrg) {
                            // of course, companySchema is implicitly not null
                            if (isSimilar(award.getSupplierName(), ocCompany.getName())) {
                                org.setVerified(true);
                            } else {
                                logger.warn(
                                        "Supplier name {} does not match company name {} in Companies House",
                                        award.getSupplierName(), ocCompany.getName()
                                );
                            }
                        }
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
    @Override
    public OCCompanySchema getCompany(String companyReference, JurisdictionEnum jurisdiction) {
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

    @Override
    public OCCompanySchema getCompany(String objectId) {
        Matcher m = Utils.ocCompanyPattern.matcher(objectId);
        if (m.matches()) {
            return getCompany(m.group(2), JurisdictionEnum.valueOf(m.group(1)));
        }
        else {
            throw new FosRuntimeException("Unable to find company");
        }
    }

    boolean isSimilar(String in1, String in2) {
        NGram nGram = new NGram(4);
        String str1 = in1.toLowerCase();
        String str2 = in2.toLowerCase();
        double similarity = 1 - nGram.distance(str1, str2);
        logger.debug("Similarity of '{}' and '{}' is reported as {}", str1, str2, similarity);
        return similarity > 0.90;
    }
}
