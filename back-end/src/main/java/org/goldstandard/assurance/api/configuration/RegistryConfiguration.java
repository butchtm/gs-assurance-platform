package org.goldstandard.assurance.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.service.external.registry.NoOpRegistryApiClient;
import org.goldstandard.assurance.api.service.external.registry.RegistryApiClient;
import org.goldstandard.assurance.api.service.external.registry.RegistryProjectFactory;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.goldstandard.assurance.api.service.external.registry.RestRegistryApiClient;
import org.goldstandard.assurance.api.service.external.registry.StubRegistryApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RegistryConfiguration {

  @Autowired private RegistryApiClientConfigurationProperties properties;

  @Bean
  @Profile("!local && !test")
  public RegistryApiClient restRegistryApiClient() {
    var host = properties.getHost();
    if (StringUtils.isEmpty(host)) {
      log.warn("no host configured for registry API so no op client will be used");
      return new NoOpRegistryApiClient();
    }
    log.info("registry api client configured with host {}", host);
    return RestRegistryApiClient.builder()
        .host(host)
        .token(properties.getToken())
        .restTemplate(new RestTemplate())
        .build();
  }

  @Bean
  @Profile("local || test")
  public RegistryApiClient stubRegistryApiClient(ObjectMapper mapper) {
    log.warn("stub registry api client configured, should only be used for local testing");
    return new StubRegistryApiClient(mapper);
  }

  @Bean
  public RegistryProjectFactory registryProjectFactory() {
    var linkTemplate = properties.getLinkTemplate();
    log.info(
        "registry project factory configured with project hyperlink template {}", linkTemplate);
    return new RegistryProjectFactory(linkTemplate);
  }

  @Bean
  public RegistryUiLinkFactory registryUiProjectLinkFactory(
      @Value("${registry.ui.host}") String host) {
    log.info("registry ui link factory configured with host {}", host);
    return new RegistryUiLinkFactory(host);
  }
}
