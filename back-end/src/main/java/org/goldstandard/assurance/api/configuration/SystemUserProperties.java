package org.goldstandard.assurance.api.configuration;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "system-user")
public class SystemUserProperties {
  private UUID uuid;
  private String username;
}
