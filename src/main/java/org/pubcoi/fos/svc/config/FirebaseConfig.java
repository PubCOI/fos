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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${fos.firebase.creds-path:file:config/credentials/firebase-credentials.json}")
    Resource firebaseCreds;

    static String credsError = "Unable to get Firebase credentials: please create these and specify their location via ${fos.firebase.creds-path}";

    @PostConstruct
    public void setup() {
        try {
            FirebaseOptions options;
            logger.info("Attempting to load Firebase properties at {}", firebaseCreds);
            if (!firebaseCreds.exists()) throw new FosCoreRuntimeException(credsError);
            try (FileInputStream serviceAccount = new FileInputStream(firebaseCreds.getFile())) {
                options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new FosCoreRuntimeException(credsError);
        }
    }

}
