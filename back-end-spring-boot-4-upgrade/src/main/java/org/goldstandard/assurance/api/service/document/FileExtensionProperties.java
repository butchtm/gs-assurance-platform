package org.goldstandard.assurance.api.service.document;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("file.extension")
public class FileExtensionProperties {
  private final Set<String> whitelist;

  public FileExtensionProperties(Set<String> whitelist) {
    this.whitelist = whitelist;
  }

  public Set<String> getWhitelist() {
    return whitelist;
  }
}
