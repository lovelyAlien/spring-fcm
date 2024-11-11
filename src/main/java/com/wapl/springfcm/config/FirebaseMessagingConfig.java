package com.wapl.springfcm.config;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;

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

    try (InputStream inputStream = new ClassPathResource("push-notification-firebase-adminsdk.json").getInputStream()) {
      return hasProxySettings() ? createCredentialsWithProxy(inputStream) : GoogleCredentials.fromStream(inputStream);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load GoogleCredentials from the Firebase configuration file.", e);
    }
  }

  @Bean
  public FirebaseOptions firebaseOptions() {
    FirebaseOptions.Builder builder = FirebaseOptions.builder()
      .setCredentials(googleCredentials());

    applyProxyIfConfigured(builder);

    return builder.build();
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
    return value != null && !value.isBlank();
  }

  private boolean hasProxySettings() {
    return hasMeaningfulValue(proxyHost) && hasMeaningfulValue(proxyPort);
  }

  private void applyProxyIfConfigured(FirebaseOptions.Builder builder) {
    if (hasProxySettings()) {
      try {
        builder.setHttpTransport(createProxyTransport(proxyHost, proxyPort));
      } catch (NumberFormatException e) {
        log.error("Invalid proxy port format: {}", proxyPort, e);
        // 필요에 따라 예외 처리 추가
      }
    }
  }

  private HttpTransport createProxyTransport(String proxyHost, String proxyPort) {
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
    return new NetHttpTransport.Builder().setProxy(proxy).build();
  }

  private GoogleCredentials createCredentialsWithProxy(InputStream inputStream) throws IOException {
    HttpTransport httpTransport = createProxyTransport(proxyHost, proxyPort);
    return GoogleCredentials.fromStream(inputStream, () -> httpTransport);
  }


}

