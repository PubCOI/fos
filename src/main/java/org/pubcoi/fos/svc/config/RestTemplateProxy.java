package org.pubcoi.fos.svc.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Service
public class RestTemplateProxy {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateProxy.class);

    SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9099));

    RestTemplate restTemplate;

    final
    Environment env;

    public RestTemplateProxy(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void setup() {
        for (String activeProfile : env.getActiveProfiles()) {
            if (activeProfile.equals("debug")) {
                logger.warn("WARNING - debug profile active, setting RestTemplate to use local proxy");
                clientHttpReq.setProxy(proxy);
            }
        }
        restTemplate = new RestTemplate(clientHttpReq);
    }

    @Bean
    public RestTemplate getTemplate() {
        return restTemplate;
    }
}
