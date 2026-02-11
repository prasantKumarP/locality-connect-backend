package com.localityconnect.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    @PostConstruct
    public void initialize() {
        try {
            // Check if FirebaseApp is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                // Try to load from service account key file
                try {
                    InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                    
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setDatabaseUrl("https://locality-connect-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .build();
                    
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase Admin SDK initialized successfully");

                    //for testing
                    System.out.println("Firebase apps: " + FirebaseApp.getApps());
                    FirebaseApp app = FirebaseApp.getInstance();
                    System.out.println("Database URL: " + app.getOptions().getDatabaseUrl());
                } catch (IOException e) {
                    // If file doesn't exist, use default credentials
                    System.out.println("Firebase service account file not found, using default credentials");
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .setDatabaseUrl("https://locality-connect-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .build();
                    
                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
            // Don't throw exception - allow app to start even if Firebase isn't configured
        }
    }
}
