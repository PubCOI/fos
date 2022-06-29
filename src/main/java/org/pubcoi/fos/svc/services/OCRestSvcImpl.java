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

import org.pubcoi.fos.svc.exceptions.core.FosCoreException;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRecordNotFoundException;
import org.pubcoi.fos.svc.models.core.JurisdictionEnum;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRestSvcImpl implements OCRestSvc {
    private static final Logger logger = LoggerFactory.getLogger(OCRestSvcImpl.class);

    final OCCachedQuerySvc cachedQuerySvc;

    final Pattern ltdPattern = Pattern.compile("(.+) Ltd(\\.?)$", Pattern.CASE_INSENSITIVE);
    final Pattern limitedPattern = Pattern.compile("(.+) Limited(\\.?)$", Pattern.CASE_INSENSITIVE);

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    String companySearchURL;
    String getGBCompanyRequestURL;

    public OCRestSvcImpl(OCCachedQuerySvc cachedQuerySvc) {
        this.cachedQuerySvc = cachedQuerySvc;
    }

    @PostConstruct
    public void setup() {
        companySearchURL = String.format("https://api.opencorporates.com/companies/search?api_token=%s&jurisdiction_code=gb&q=%%s", apiToken);
        getGBCompanyRequestURL = String.format("https://api.opencorporates.com/companies/%%s/%%s?api_token=%s", apiToken);
    }

    @Override
    public OCWrapper doCompanySearch(String companyReference) throws FosCoreException {
        return doCompanySearch(companyReference, 0);
    }

    private OCWrapper doCompanySearch(String companyReference, int previousSearches) throws FosCoreException {
        String queryURL = String.format(companySearchURL, URLEncoder.encode(companyReference, StandardCharsets.UTF_8));
        OCWrapper wrapper = cachedQuerySvc.doRequest(queryURL);
        if (previousSearches < 2 && null == wrapper.getResults().getCompany() && wrapper.getResults().getCompanies().isEmpty()) {
            // consider alternatives ...
            // ltd > limited > (no ltd / limited)
            Matcher ltdMatcher = ltdPattern.matcher(companyReference);
            if (ltdMatcher.matches()) {
                String rootName = ltdMatcher.group(1);
                logger.debug("'{}' matched Ltd pattern: re-searching with 'Limited' instead", rootName);
                return doCompanySearch(String.format("%s Limited", rootName), previousSearches + 1);
            }
            Matcher limitedMatcher = limitedPattern.matcher(companyReference);
            if (limitedMatcher.matches()) {
                String rootName = limitedMatcher.group(1);
                logger.debug("'{}' matched Limited pattern: re-searching with no Ltd / Limited", rootName);
                // todo we should first go back and search by Ltd if we've not already done so
                return doCompanySearch(rootName, previousSearches + 1);
            }
        }
        return wrapper;
    }

    @Override
    public OCWrapper getCompany(String companyReference, JurisdictionEnum jurisdiction) throws FosCoreRecordNotFoundException {
        String queryURL = String.format(getGBCompanyRequestURL, jurisdiction, URLEncoder.encode(companyReference, StandardCharsets.UTF_8));
        try {
            return cachedQuerySvc.doRequest(queryURL);
        } catch (FosCoreException e) {
            throw new FosCoreRecordNotFoundException(e);
        }
    }
}
