package org.pubcoi.fos.svc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pubcoi.fos.svc.repos.mdb.OCCachedQueryRepo;
import org.pubcoi.fos.svc.models.mdb.OCCachedQuery;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
    public OCWrapper doRequest(String queryURL) {
        OCCachedQuery query = cachedQueryRepo.getByIdAndRequestDTAfter(
                OCCachedQuery.getHash(queryURL), OffsetDateTime.now().minus(3, ChronoUnit.MONTHS)
        );
        if (null != query) {
            try {
                return objectMapper.readValue(query.getResponse(), OCWrapper.class);
            } catch (IOException e) {
                logger.error(String.format("Unable to cast cached object back to " +
                        "OCWrapper object, will perform another request (err: %s)", e.getMessage()), e);
            }
        }
        OCWrapper wrapper = restTemplate.getForObject(queryURL, OCWrapper.class);
        try {
            OCCachedQuery cachedQuery = new OCCachedQuery(queryURL, objectMapper.writeValueAsBytes(wrapper));
            logger.debug("Caching OC request {} ({})", cachedQuery.getId(), cachedQuery.getQueryURL());
            cachedQueryRepo.save(cachedQuery);
        } catch (JsonProcessingException e) {
            logger.error("Unable to write OCWrapper to byte array, cannot cache response");
        }
        return wrapper;
    }
}
