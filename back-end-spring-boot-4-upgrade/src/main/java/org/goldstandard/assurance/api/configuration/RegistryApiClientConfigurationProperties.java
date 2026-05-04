package org.goldstandard.assurance.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "registry.project.api")
public class RegistryApiClientConfigurationProperties {

  private String host;
  private String token;
  private String linkTemplate;
}
