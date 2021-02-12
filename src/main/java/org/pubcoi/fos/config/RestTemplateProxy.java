package org.pubcoi.fos.config;


import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Service
public class RestTemplateProxy {

    SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9099));

    RestTemplate restTemplate;

    @PostConstruct
    public void setup() {
        clientHttpReq.setProxy(proxy);
        restTemplate = new RestTemplate(clientHttpReq);
    }

    @Bean
    public RestTemplate getTemplate() {
        return restTemplate;
    }
}
