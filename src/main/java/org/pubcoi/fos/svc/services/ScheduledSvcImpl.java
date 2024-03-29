/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import info.debatty.java.stringsimilarity.NGram;
import org.pubcoi.cdm.cf.ReferenceTypeE;
import org.pubcoi.fos.svc.exceptions.core.FosCoreException;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRecordNotFoundException;
import org.pubcoi.fos.svc.models.core.*;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.repos.mdb.OrganisationsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.TasksMDBRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.regex.Matcher;

@Service
public class ScheduledSvcImpl implements ScheduledSvc {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledSvcImpl.class);

    final AwardsMDBRepo awardsMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final OCRestSvc ocRestSvc;
    final RestTemplate restTemplate;
    final OrganisationsMDBRepo orgMDBRepo;
    final TasksMDBRepo tasksMDBRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final MnisSvc mnisSvc;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    public ScheduledSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            OCCompaniesRepo ocCompanies,
            OCRestSvc ocRestSvc,
            RestTemplate restTemplate,
            OrganisationsMDBRepo orgMDBRepo,
            TasksMDBRepo tasksMDBRepo,
            OrganisationsGraphRepo organisationsGraphRepo,
            MnisSvc mnisSvc) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.ocCompanies = ocCompanies;
        this.ocRestSvc = ocRestSvc;
        this.restTemplate = restTemplate;
        this.orgMDBRepo = orgMDBRepo;
        this.tasksMDBRepo = tasksMDBRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.mnisSvc = mnisSvc;
    }

    @Override
    public void flagPotentialConflicts() throws FosCoreException {
        for (OrganisationNode organisationNode : organisationsGraphRepo.findAllNotHydrating()) {
            mnisSvc.searchInterestsForConflicts(organisationNode.getName()).forEach(c -> {
                DRResolvePotentialCOITask task = new DRResolvePotentialCOITask(organisationNode, c);
                if (!tasksMDBRepo.existsById(task.getId())) {
                    tasksMDBRepo.save(task);
                    logger.info(Ansi.Yellow.format(
                            "Saved task %s to resolve potential interest between org %s and interest %s",
                            task.getId(), organisationNode.getFosId(), c)
                    );
                }
            });
        }
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
                    OCCompanySchema ocCompany = null;
                    try {
                        ocCompany = (null != award.getOrgReferenceType()
                                && award.getOrgReferenceType().equals(ReferenceTypeE.COMPANIES_HOUSE)
                                && null != award.getOrgReference()
                        ) ? getCompany(award.getOrgReference(), JurisdictionEnum.gb) : null;
                    } catch (FosCoreException e) {
                        logger.info("Unable to find record for company " + award.getOrgReference());
                    }

                    FosOrganisation org = (null == ocCompany) ? new FosNonCanonicalOrg(award) :
                            new FosCanonicalOrg(ocCompany);

                    // save new org if it doesn't exist
                    if (!orgMDBRepo.existsByFosId(org.getFosId())) {
                        if (org instanceof FosCanonicalOrg) {
                            // of course, companySchema is implicitly not null
                            if (isSimilar(award.getSupplierName(), ocCompany.getName())) {
                                logger.debug(Ansi.Green.format("Marking organisation %s as verified", org.getFosId()));
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
                            tasksMDBRepo.save(new DRTask(FosTaskType.resolve_company, org));
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
    public OCCompanySchema getCompany(@NonNull String companyReference, @NonNull JurisdictionEnum jurisdiction) throws FosCoreRecordNotFoundException {
        Objects.requireNonNull(companyReference);
        Objects.requireNonNull(jurisdiction);

        // see if the company exists on our DB first
        OCCompanySchema mdbCompany = ocCompanies.findByCompanyNumberAndJurisdictionCode(companyReference, jurisdiction.toString());
        if (null != mdbCompany) return mdbCompany;

        // then see if it exists on OC
        OCWrapper companyLookup = ocRestSvc.getCompany(companyReference, jurisdiction);
        if (null != companyLookup.getResults() && null != companyLookup.getResults().getCompany()) {
            ocCompanies.save(companyLookup.getResults().getCompany());
        }
        return null != companyLookup.getResults() ? companyLookup.getResults().getCompany() : null;
    }

    @Override
    public OCCompanySchema getCompany(String objectId) throws FosCoreRecordNotFoundException {
        Matcher m = Utils.ocCompanyPattern.matcher(objectId);
        if (m.matches()) {
            return getCompany(m.group(2), JurisdictionEnum.valueOf(m.group(1)));
        } else {
            throw new FosCoreRecordNotFoundException("Unable to find company");
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
