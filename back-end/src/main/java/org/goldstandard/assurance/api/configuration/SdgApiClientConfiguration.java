package org.goldstandard.assurance.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.service.external.sdg.NoOpSdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.RestSdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.SdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.StubSdgApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class SdgApiClientConfiguration {

  @Bean
  @Profile("!local && !test")
  public SdgApiClient restSdgApiClient(SdgApiClientConfigurationProperties properties) {
    return buildRestClient(properties);
  }

  @Bean
  @Profile("local && !test")
  @ConditionalOnProperty(prefix = "sdg.tool.project.api", name = "enabled", havingValue = "true")
  public SdgApiClient localRestSdgApiClient(SdgApiClientConfigurationProperties properties) {
    return buildRestClient(properties);
  }

  @Bean
  @Profile("local && !test")
  @ConditionalOnProperty(
      prefix = "sdg.tool.project.api",
      name = "enabled",
      havingValue = "false",
      matchIfMissing = true)
  public SdgApiClient localStubSdgApiClient(ObjectMapper mapper) {
    log.warn("stub sdg api client configured, should only be used for local testing");
    return new StubSdgApiClient(mapper);
  }

  @Bean
  @Profile("test")
  public SdgApiClient testStubSdgApiClient(ObjectMapper mapper) {
    log.warn("stub sdg api client configured, should only be used for local testing");
    return new StubSdgApiClient(mapper);
  }

  private SdgApiClient buildRestClient(SdgApiClientConfigurationProperties properties) {
    var host = properties.getHost();
    if (StringUtils.isEmpty(host)) {
      log.warn("no host configured for sdg API so no op client will be used");
      return new NoOpSdgApiClient();
    }
    log.info("sdg api client configured with host {}", host);
    return RestSdgApiClient.builder()
        .host(host)
        .token(properties.getToken())
        .restTemplate(
            new RestTemplate(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())))
        .build();
  }
}
