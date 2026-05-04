package org.goldstandard.assurance.api.configuration.ratelimiting;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.pub.PublicRateLimitService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "public-rate-limit.enabled", havingValue = "true")
public class PublicRateLimitConfiguration {

  private final PublicRateLimitProperties properties;

  @Bean
  public PublicRateLimitService commentRateLimitService() {
    return new PublicRateLimitService(properties.getComment(), properties.getIpWhitelist());
  }

  @Bean
  public PublicRateLimitService commentPiiAnalysisRateLimitService() {
    return new PublicRateLimitService(
        properties.getCommentPiiAnalysis(), properties.getIpWhitelist());
  }

  @Bean
  public PublicRateLimitService documentUploadRateLimitService() {
    return new PublicRateLimitService(properties.getDocument(), properties.getIpWhitelist());
  }

  @Bean
  public PublicRateLimitService projectDocumentRateLimitService() {
    return new PublicRateLimitService(properties.getProjectDocument(), properties.getIpWhitelist());
  }
}
