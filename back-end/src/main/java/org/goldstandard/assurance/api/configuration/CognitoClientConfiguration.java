package org.goldstandard.assurance.api.configuration;

import java.net.URI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * For local development AWS credentials can be set as environment variables in the IntelliJ run
 * configuration. AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY with session token if required for
 * temporary credentials AWS_SESSION_TOKEN
 */
@Configuration
@Getter
@Slf4j
public class CognitoClientConfiguration {

  @Value("${cognito.region.name}")
  private String regionName;

  @Value("${cognito.app.client.id}")
  private String clientId;

  @Value("${cognito.endpoint.override:}")
  private String endpointOverride;

  @Bean
  public CognitoIdentityProviderClient getCognitoClient() {
    var credentialsProvider = DefaultCredentialsProvider.builder().build();
    var builder =
        CognitoIdentityProviderClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(regionName));
    log.info("configured S3 client with region {}", regionName);
    if (StringUtils.isNotEmpty(endpointOverride)) {
      builder.endpointOverride(URI.create(endpointOverride));
      log.info("configured cognito client with endpoint override {}", endpointOverride);
    }
    return builder.build();
  }
}
