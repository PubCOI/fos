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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pubcoi.fos.svc.exceptions.FosCoreException;
import org.pubcoi.fos.svc.models.mdb.OCCachedQuery;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.pubcoi.fos.svc.repos.mdb.OCCachedQueryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class OCCachedQuerySvcImpl implements OCCachedQuerySvc {
    private static final Logger logger = LoggerFactory.getLogger(OCCachedQuerySvcImpl.class);

    final OCCachedQueryRepo cachedQueryRepo;
    final RestTemplate restTemplate;
    final ObjectMapper objectMapper;

    public OCCachedQuerySvcImpl(
            OCCachedQueryRepo cachedQueryRepo,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.cachedQueryRepo = cachedQueryRepo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public OCWrapper doRequest(String queryURL) throws FosCoreException {
        logger.debug(Ansi.Cyan.format("Requesting URL %s", OCCachedQuery.redact(queryURL)));
        OCCachedQuery query = cachedQueryRepo.getByIdAndRequestDTAfter(
                OCCachedQuery.getHash(queryURL), OffsetDateTime.now().minus(3, ChronoUnit.MONTHS)
        );
        if (null != query) {
            if (query.getResponseCode() != 200) {
                throw new FosCoreException(String.format("Unable to pull back a valid response for hash %s", query.getId()));
            }
            try {
                return objectMapper.readValue(query.getResponse(), OCWrapper.class);
            } catch (IOException e) {
                throw new FosCoreException(String.format(
                        "Unable to cast cached object for hash %s back to OCWrapper object, will perform another request (err: %s)",
                        query.getId(), e.getMessage()
                ), e);
            }
        }
        OCCachedQuery cachedQuery = new OCCachedQuery(queryURL);
        OCWrapper wrapper;
        try {
            ResponseEntity<OCWrapper> response = restTemplate.exchange(queryURL, HttpMethod.GET, null, OCWrapper.class);
            cachedQuery.setResponseCode(response.getStatusCode().value());
            wrapper = response.getBody();
            cachedQuery.setResponse(objectMapper.writeValueAsBytes(wrapper));
        } catch (HttpClientErrorException e) {
            cachedQuery.setResponseCode(e.getRawStatusCode());
            throw new FosCoreException(String.format("Unable to make request for hash %s: %s", cachedQuery.getId(), e.getMessage()), e);
        } catch (JsonProcessingException e) {
            throw new FosCoreException(String.format("Unable to process response body for hash %s: %s", cachedQuery.getId(), e.getMessage()));
        } finally {
            logger.debug("Caching OC request {} ({})", cachedQuery.getId(), cachedQuery.getQueryURL());
            cachedQueryRepo.save(cachedQuery);
        }
        return wrapper;
    }
}
