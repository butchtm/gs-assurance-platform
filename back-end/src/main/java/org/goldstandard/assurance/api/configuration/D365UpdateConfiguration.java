package org.goldstandard.assurance.api.configuration;

import java.time.Duration;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.goldstandard.assurance.api.service.external.ExternalSystem;
import org.goldstandard.assurance.api.service.external.ExternalUpdateConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "d365.update")
public class D365UpdateConfiguration implements ExternalUpdateConfig {
  private Duration interval;
  private Duration retryInterval;
  private Integer maxRetries;
  private Integer maxRequests;
  private Collection<Integer> abortCodes;

  @Override
  public ExternalSystem getSystem() {
    return ExternalSystem.D365;
  }
}
