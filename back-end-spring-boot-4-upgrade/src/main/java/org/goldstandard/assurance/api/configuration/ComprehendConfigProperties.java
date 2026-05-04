package org.goldstandard.assurance.api.configuration;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "comprehend")
public class ComprehendConfigProperties {
  private String region;
  private Collection<String> typesToIgnore;
  private Float defaultScoreThreshold;
  private Map<String, Float> scoreThresholdOverrides;
}
