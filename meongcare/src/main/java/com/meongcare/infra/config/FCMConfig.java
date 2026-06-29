package com.meongcare.infra.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FCMConfig {

    private static final String FIREBASE_KEY_PATH = "firebase/meongcare-firebase-key.json";

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials googleCredentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        String credentialsJson = System.getenv("FIREBASE_CREDENTIALS");
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            InputStream stream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
            return GoogleCredentials.fromStream(stream);
        }
        ClassPathResource resource = new ClassPathResource(FIREBASE_KEY_PATH);
        InputStream refreshToken = resource.getInputStream();
        return GoogleCredentials.fromStream(refreshToken);
    }
}
