package org.pubcoi.fos.svc.config;

import org.pubcoi.fos.svc.services.ApplicationStatusBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class LocalApplicationConfig {

    final Environment env;

    public LocalApplicationConfig(Environment env) {
        this.env = env;
    }

    @Bean
    ApplicationStatusBean getApplicationStatus() {
        ApplicationStatusBean status = new ApplicationStatusBean();
        for (String activeProfile : env.getActiveProfiles()) {
            if (activeProfile.equalsIgnoreCase("debug")) {
                status.setDebug(true);
            }
            if (activeProfile.equalsIgnoreCase("batch")) {
                status.setBatch(true);
            }
        }
        return status;
    }

}
