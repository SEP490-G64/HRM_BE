package com.example.hrm_be.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
  @Bean
  public FirebaseMessaging firebaseMessaging() throws IOException {
    // Load the service account key JSON file
    try (InputStream serviceAccount =
        new ClassPathResource("firebase-service-account.json").getInputStream()) {
      GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

      // Initialize Firebase options
      FirebaseOptions firebaseOptions =
          FirebaseOptions.builder().setCredentials(googleCredentials).build();

      // Initialize FirebaseApp if not already initialized
      FirebaseApp app;
      if (FirebaseApp.getApps().isEmpty()) {
        app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
      } else {
        app = FirebaseApp.getInstance("my-app");
      }

      // Return the FirebaseMessaging instance
      return FirebaseMessaging.getInstance(app);
    }
  }
}
