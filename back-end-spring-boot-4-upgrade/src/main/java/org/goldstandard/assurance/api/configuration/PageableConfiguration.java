package org.goldstandard.assurance.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfiguration {

  // Include this so that pages are indexed from 1 instead of 0 from the client perspective.
  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer paginationCustomizer() {
    return pageableResolver -> pageableResolver.setOneIndexedParameters(true);
  }
}
