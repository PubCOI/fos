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

package org.pubcoi.fos.svc.config;


import org.pubcoi.fos.svc.config.converters.FullNoticeResponseMessageConverter;
import org.pubcoi.fos.svc.config.converters.NoticeSearchResponseMessageConverter;
import org.pubcoi.fos.svc.services.XslSvc;
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

    final XslSvc xslSvc;
    final Environment env;
    RestTemplate restTemplate;

    SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9099));

    public RestTemplateProxy(XslSvc xslSvc, Environment env) {
        this.xslSvc = xslSvc;
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
        restTemplate.getMessageConverters().add(new NoticeSearchResponseMessageConverter(xslSvc));
        restTemplate.getMessageConverters().add(new FullNoticeResponseMessageConverter(xslSvc));
    }

    @Bean
    public RestTemplate getTemplate() {
        return restTemplate;
    }
}
