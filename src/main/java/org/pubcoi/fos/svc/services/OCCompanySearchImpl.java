package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OCCompanySearchImpl implements OCCompanySearch {
    private static final Logger logger = LoggerFactory.getLogger(OCCompanySearchImpl.class);

    final RestTemplate restTemplate;

    @Value("${pubcoi.fos.opencorporates.api-key}")
    String apiToken;

    String companySearchURL;

    public OCCompanySearchImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void setup() {
        companySearchURL = String.format("https://api.opencorporates.com/companies/search?api_token=%s&jurisdiction_code=gb&q=%%s", apiToken);
    }

    @Override
    public OCWrapper doSearch(String query) {
        String queryURL = String.format(companySearchURL, URLEncoder.encode(query, StandardCharsets.UTF_8));
        return restTemplate.getForObject(queryURL, OCWrapper.class);
    }
}
