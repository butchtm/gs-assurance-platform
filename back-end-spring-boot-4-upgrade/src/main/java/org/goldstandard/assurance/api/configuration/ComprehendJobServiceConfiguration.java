package org.goldstandard.assurance.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "comprehend.poll-for-completion")
public class ComprehendJobServiceConfiguration {
  private Integer syncInterval;
}
