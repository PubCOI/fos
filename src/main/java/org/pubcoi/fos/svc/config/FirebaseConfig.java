package org.pubcoi.fos.svc.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.io.Resources;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.pubcoi.fos.svc.exceptions.FOSRuntimeException;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    final String firebaseCredsPath = "credentials/firebase-credentials.json";

    @PostConstruct
    public void setup() {
        try {
            FirebaseOptions options;
            try (FileInputStream serviceAccount = new FileInputStream(Resources.getResource(firebaseCredsPath).getFile())) {
                options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new FOSRuntimeException(String.format("Unable to get Firebase credentials: please create these and download them to %s", firebaseCredsPath));
        }
    }

}
