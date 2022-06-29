package org.pubcoi.fos.svc.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pubcoi.fos.svc.exceptions.core.FosCoreException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.dto.VerifyCompanySearchRequestDTO;
import org.pubcoi.fos.svc.models.dto.VerifyCompanySearchResponse;
import org.pubcoi.fos.svc.models.oc.OCCompanySchemaWrapper;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.pubcoi.fos.svc.repos.mdb.OCCachedQueryRepo;
import org.pubcoi.fos.svc.repos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.svc.services.OCRestSvc;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For any external batch jobs eg external services using OC data
 *
 */
@RestController
@Profile("standalone")
public class BatchRest {

    private static final Logger logger = LoggerFactory.getLogger(BatchRest.class);

    final FosAuthProvider authProvider;
    final OCRestSvc ocRestSvc;
    final OCCachedQueryRepo cachedQueryRepo;
    final ObjectMapper objectMapper;
    final OCCompaniesRepo companiesRepo;

    public BatchRest(FosAuthProvider authProvider, OCRestSvc ocRestSvc, OCCachedQueryRepo cachedQueryRepo, ObjectMapper objectMapper, OCCompaniesRepo companiesRepo) {
        this.authProvider = authProvider;
        this.ocRestSvc = ocRestSvc;
        this.cachedQueryRepo = cachedQueryRepo;
        this.objectMapper = objectMapper;
        this.companiesRepo = companiesRepo;
    }

    /**
     * Direct search on opencorporates for particular search term - returns postcode if singe match,
     * otherwise returns an error
     * If this fails & there are multiple matches, a separate search should be performed against the cached result
     * @param requestDTO
     * @param authToken
     * @param returning
     * @return
     */
    // from a list of companies:  jq -R -c '{companyId: .}' < all-companies | curl ...
    // eg jq -R -c '{companyId: .}' < all-companies | tr '\n' '\0' | \
    //  xargs -I{} -0 curl "http://127.0.0.1:8080/api/batch/search/oc/companies?returning=postcode" \
    //  -H"content-type: application/json" -H"authToken: example" -w "\n" -d{}
    @PostMapping("/api/batch/search/oc/companies")
    public String doCompanyVerifySearch(
            @RequestBody VerifyCompanySearchRequestDTO requestDTO,
            @RequestHeader String authToken,
            @RequestParam String returning,
            @RequestParam(defaultValue = "false", required = false) Boolean forceResponse
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        logger.debug("Search company: {}; Performing search on behalf of {}", requestDTO, user);

        // for now only return postcode
        if (!returning.equals("postcode")) {
            throw new FosEndpointException("Only able to return postcodes for now");
        }
        try {
            OCWrapper wrapper = ocRestSvc.doCompanySearch(requestDTO.getCompanyId());
            addCompanies(wrapper);
            if (wrapper.getResults().getCompanies().isEmpty()) {
                logger.info("Error getting postcode: no matches");
                return "";
            }
            if (wrapper.getResults().getCompanies().size() > 1) {
                if (forceResponse) {
                    return returnPostcode(requestDTO.getCompanyId(),
                            wrapper.getResults().getCompanies().stream()
                                    .sorted(Comparator.comparing(c ->
                                            c.getCompany().getCreatedAt())
                                    ).collect(Collectors.toList()).get(0));
                }
                return "ERROR:MULTIPLE_MATCHES";
            }
            // todo - safe unwrap
            return returnPostcode(requestDTO.getCompanyId(), wrapper.getResults().getCompanies().get(0));
        } catch (FosCoreException e) {
            logger.error("Unable to perform search");
            throw new FosEndpointException(e);
        }
    }

    private String returnPostcode(String companyId, OCCompanySchemaWrapper ocCompanySchemaWrapper) {
        if (null != ocCompanySchemaWrapper.getCompany().getRegisteredAddress() && null != ocCompanySchemaWrapper.getCompany().getRegisteredAddress().getPostalCode()) {
            return ocCompanySchemaWrapper.getCompany().getRegisteredAddress().getPostalCode();
        }
        else {
            return "";
        }
    }

    /**
     * get all company search responses straight from the raw / cached ocwrapper objects
     * @return
     */
    @GetMapping("/api/batch/search/companies/debug_fetch-all")
    public List<OCWrapper> getResponses() {
        List<OCWrapper> responses = new ArrayList<>();
        cachedQueryRepo.findAll().forEach(r -> {
            try {
                responses.add(objectMapper.readValue(r.getResponse(), OCWrapper.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return responses;
    }

    /**
     * update our internal list of companies based on all the stored ocwrapper responses
     */
    @GetMapping("/api/batch/search/companies/debug_update-all")
    public void updateResponses() {
        cachedQueryRepo.findAll().forEach(r -> {
            try {
                OCWrapper wrapper = objectMapper.readValue(r.getResponse(), OCWrapper.class);
                addCompanies(wrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addCompanies(OCWrapper wrapper) {
        if (null != wrapper && null != wrapper.getResults()
                && null != wrapper.getResults().getCompanies() && !wrapper.getResults().getCompanies().isEmpty()) {
            for (OCCompanySchemaWrapper company : wrapper.getResults().getCompanies()) {
                companiesRepo.save(company.getCompany());
            }
        }
    }

}
