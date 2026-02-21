package com.localityconnect.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final String DATABASE_URL =
            "https://locality-connect-default-rtdb.asia-southeast1.firebasedatabase.app";

    @PostConstruct
    public void initialize() {
        try {

            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            FirebaseOptions options;

            // 🔥 1️⃣ Try environment variable first (Render production)
            String firebaseEnv = System.getenv("FIREBASE_SERVICE_ACCOUNT");

            if (firebaseEnv != null && !firebaseEnv.isBlank()) {

                System.out.println("Initializing Firebase using ENV variable");

                InputStream serviceAccount =
                        new ByteArrayInputStream(
                                firebaseEnv.getBytes(StandardCharsets.UTF_8)
                        );

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl(DATABASE_URL)
                        .build();

            } else {

                // 🔥 2️⃣ Fallback to local JSON file (development)
                System.out.println("FIREBASE_SERVICE_ACCOUNT not found, trying local file");

                InputStream serviceAccount =
                        new ClassPathResource("firebase-service-account.json")
                                .getInputStream();

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl(DATABASE_URL)
                        .build();
            }

            FirebaseApp.initializeApp(options);

            System.out.println("Firebase initialized successfully");
            System.out.println("Firebase apps: " + FirebaseApp.getApps());

        } catch (Exception e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
            // Do NOT crash app
        }
    }
}