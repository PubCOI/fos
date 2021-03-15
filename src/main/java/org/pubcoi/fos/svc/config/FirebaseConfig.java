package org.pubcoi.fos.svc.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
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
            if (!firebaseCreds.exists()) throw new FosRuntimeException(credsError);
            try (FileInputStream serviceAccount = new FileInputStream(firebaseCreds.getFile())) {
                options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new FosRuntimeException(credsError);
        }
    }

}
