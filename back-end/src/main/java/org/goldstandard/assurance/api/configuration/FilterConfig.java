package org.goldstandard.assurance.api.configuration;

import org.goldstandard.assurance.api.filter.ClearMdcFilter;
import org.goldstandard.assurance.api.filter.CorrelationIdMdcPopulatorFilter;
import org.goldstandard.assurance.api.filter.RequestMdcPopulatorFilter;
import org.goldstandard.assurance.api.filter.UserMdcPopulatorFilter;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<CorrelationIdMdcPopulatorFilter> correlationIdFilter() {
    FilterRegistrationBean<CorrelationIdMdcPopulatorFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new CorrelationIdMdcPopulatorFilter());
    bean.addUrlPatterns("*");
    bean.setOrder(1);
    bean.setName("correlationIdFilter");
    return bean;
  }

  @Bean
  public FilterRegistrationBean<UserMdcPopulatorFilter> usernameFilter(
      AuthenticatedUserSupplier userSupplier) {
    FilterRegistrationBean<UserMdcPopulatorFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new UserMdcPopulatorFilter(userSupplier));
    bean.addUrlPatterns("*");
    bean.setOrder(2);
    bean.setName("loggingContextFilter");
    return bean;
  }

  @Bean
  public FilterRegistrationBean<RequestMdcPopulatorFilter> requestFilter() {
    FilterRegistrationBean<RequestMdcPopulatorFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new RequestMdcPopulatorFilter());
    bean.addUrlPatterns("*");
    bean.setOrder(3);
    bean.setName("requestMdcPopulator");
    return bean;
  }

  @Bean
  public FilterRegistrationBean<ClearMdcFilter> clearMdcFilter() {
    FilterRegistrationBean<ClearMdcFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new ClearMdcFilter());
    bean.addUrlPatterns("*");
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    bean.setName("clearMdcFilter");
    return bean;
  }
}
