package org.goldstandard.assurance.api.configuration;

import java.net.URI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientAsyncConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2AsyncClient;

@Getter
@Configuration
@Slf4j
public class NotificationConfiguration {
  @Value("${notification.source}")
  private String notificationSource;

  @Value("${notification.contact-mapping.enabled}")
  private boolean contactMappingEnabled;

  @Value("${notification.ses.region.name}")
  private String regionName;

  @Value("${notification.ses.endpoint.override:}")
  private String endpointOverride;

  @Bean
  public SesV2AsyncClient sesAsyncClient(ClientAsyncConfiguration asyncConfiguration) {
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    var builder =
        SesV2AsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .asyncConfiguration(asyncConfiguration)
            .region(Region.of(regionName));
    log.info("configured SES async client with region {}", regionName);
    if (StringUtils.isNotEmpty(endpointOverride)) {
      builder.endpointOverride(URI.create(endpointOverride));
      log.info("configured SES async client with endpoint override {}", endpointOverride);
    }
    return builder.build();
  }
}
