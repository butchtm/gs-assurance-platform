package org.goldstandard.assurance.api.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ComprehendClientConfiguration {
  private final ComprehendConfigProperties properties;

  @Bean
  @Profile("!local && !test")
  public ComprehendClient comprehendClient() {
    log.info("configured comprehend client with region {}", properties.getRegion());
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    return ComprehendClient.builder()
        .region(Region.of(properties.getRegion()))
        .credentialsProvider(credentialsProvider)
        .build();
  }
}
