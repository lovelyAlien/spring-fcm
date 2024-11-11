package com.wapl.springfcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
//@ConditionalOnProperty(prefix = "firebase", name = "enable", havingValue = "true")
@Configuration
public class FirebaseMessagingConfig {

  private final String proxyHost;
  private final String proxyPort;

  public FirebaseMessagingConfig(@Value("${firebase.proxy.host:}") String proxyHost,
                                 @Value("${firebase.proxy.port:}") String proxyPort) {
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
  }

  @Bean
  public GoogleCredentials googleCredentials() {
    try {
      GoogleCredentials googleCredentials = GoogleCredentials
        .fromStream(new ClassPathResource(("push-notification-firebase-adminsdk.json")).getInputStream());
      return googleCredentials;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load GoogleCredentials from the Firebase configuration file.", e);
    }
  }

  @Bean
  public FirebaseOptions firebaseOptions() {
    FirebaseOptions.Builder firebaseOptionsBuilder = FirebaseOptions.builder();
    firebaseOptionsBuilder.setCredentials(googleCredentials());
    FirebaseOptions firebaseOptions = firebaseOptionsBuilder.build();
    return firebaseOptions;
  }

  @Bean
  public FirebaseApp firebaseApp() {
    if (FirebaseApp.getApps().isEmpty()) {
      return FirebaseApp.initializeApp(firebaseOptions());
    } else {
      return FirebaseApp.getInstance();
    }
  }

  @Bean
  public FirebaseMessaging firebaseMessaging() {
    FirebaseMessaging instance = FirebaseMessaging.getInstance(firebaseApp());
    return instance;
  }

  private boolean hasMeaningfulValue(String value) {
    if(value == null || value.isBlank()) {
      return false;
    }
    return true;
  }
}

