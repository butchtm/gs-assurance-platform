package org.goldstandard.assurance.api.configuration;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.ratelimiting.RateLimitInterceptor;
import org.goldstandard.assurance.api.filter.RemoteAddressExtractor;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.pub.PublicRateLimitService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

  private final RemoteAddressExtractor remoteAddressExtractor;
  private final Collection<PublicRateLimitService> services;
  private final AuthenticatedUserSupplier userSupplier;

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void addInterceptors(@Nonnull InterceptorRegistry registry) {
    log.info("configuring {} public rate limit interceptors", services.size());
    services.forEach(service -> configureRateLimitInterceptor(registry, service));
  }

  private void configureRateLimitInterceptor(
      InterceptorRegistry registry, PublicRateLimitService service) {
    log.info(
        "configuring public rate limit interceptor for path patterns {}",
        service.getPathPatterns());
    registry
        .addInterceptor(new RateLimitInterceptor(service, remoteAddressExtractor, userSupplier))
        .addPathPatterns(service.getPathPatterns());
  }
}
