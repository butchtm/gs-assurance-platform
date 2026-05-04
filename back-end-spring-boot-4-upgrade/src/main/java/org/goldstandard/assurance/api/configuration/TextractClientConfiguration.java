package org.goldstandard.assurance.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
@Slf4j
public class TextractClientConfiguration {

  @Value("${textract.region}")
  private String regionName;

  @Bean
  @Profile("!local && !test")
  public TextractClient getTextractClient() {
    log.info("configured textract client with region {}", regionName);
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    return TextractClient.builder()
        .region(Region.of(regionName))
        .credentialsProvider(credentialsProvider)
        .build();
  }
}
