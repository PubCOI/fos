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
