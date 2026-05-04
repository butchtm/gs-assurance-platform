package org.goldstandard.assurance.api.configuration.ratelimiting;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.service.pub.IpWhitelist;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("public-rate-limit")
@Getter
@Setter
public class PublicRateLimitProperties {
  private boolean enabled;
  private Allowlist allowlist;
  private RateLimitEndpointProperties commentPiiAnalysis;
  private RateLimitEndpointProperties comment;
  private RateLimitEndpointProperties document;
  private RateLimitEndpointProperties projectDocument;

  public IpWhitelist getIpWhitelist() {
    return new IpWhitelist(allowlist.getV4CDIRsAsCollection());
  }

  @Getter
  @Setter
  public static class RateLimitEndpointProperties {
    private List<String> pathPatterns;
    private Integer requestsPerInterval;
    private Duration refillInterval;
  }

  @Getter
  @Setter
  private static class Allowlist {
    private String v4cdirs;

    public Collection<String> getV4CDIRsAsCollection() {
      if (StringUtils.isEmpty(v4cdirs)) {
        return Collections.emptyList();
      }
      return Stream.of(v4cdirs.split(",")).map(String::trim).toList();
    }
  }
}
