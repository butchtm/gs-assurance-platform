package org.goldstandard.assurance.api.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.goldstandard.assurance.api.service.external.sdg.NoOpSdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.RestSdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.SdgApiClient;
import org.goldstandard.assurance.api.service.external.sdg.StubSdgApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SdgApiClientConfigurationTest {

  @Test
  void shouldUseStubInLocalProfileWhenEnabledPropertyIsMissing() {
    runWithProfiles(new String[] {"local"}, this::assertStub);
  }

  @Test
  void shouldUseStubInLocalProfileWhenDisabled() {
    runWithProfiles(new String[] {"local"}, this::assertStub, "sdg.tool.project.api.enabled=false");
  }

  @Test
  void shouldUseRestClientInLocalProfileWhenEnabled() {
    runWithProfiles(
        new String[] {"local"},
        context -> {
          assertThat(context).hasSingleBean(SdgApiClient.class);
          assertThat(context.getBean(SdgApiClient.class)).isInstanceOf(RestSdgApiClient.class);
        },
        "sdg.tool.project.api.enabled=true");
  }

  @Test
  void shouldKeepStubInTestProfileWhenLocalAndSdgRestAreEnabled() {
    runWithProfiles(
        new String[] {"local", "test"}, this::assertStub, "sdg.tool.project.api.enabled=true");
  }

  @Test
  void shouldUseRestClientOutsideLocalAndTestProfilesWhenHostIsConfigured() {
    runWithProfiles(
        new String[0],
        context -> {
          assertThat(context).hasSingleBean(SdgApiClient.class);
          assertThat(context.getBean(SdgApiClient.class)).isInstanceOf(RestSdgApiClient.class);
        });
  }

  @Test
  void shouldUseNoOpClientOutsideLocalAndTestProfilesWhenHostIsMissing() {
    runWithProfiles(
        new String[0],
        context -> {
          assertThat(context).hasSingleBean(SdgApiClient.class);
          assertThat(context.getBean(SdgApiClient.class)).isInstanceOf(NoOpSdgApiClient.class);
        },
        "test.sdg.host.missing=true");
  }

  private void assertStub(AssertableApplicationContext context) {
    assertThat(context).hasSingleBean(SdgApiClient.class);
    assertThat(context.getBean(SdgApiClient.class)).isInstanceOf(StubSdgApiClient.class);
  }

  private void runWithProfiles(
      String[] profiles,
      Consumer<AssertableApplicationContext> assertions,
      String... propertyValues) {
    new ApplicationContextRunner()
        .withUserConfiguration(SdgApiClientConfiguration.class, TestDependencies.class)
        .withInitializer(
            (ApplicationContextInitializer<ConfigurableApplicationContext>)
                context -> context.getEnvironment().setActiveProfiles(profiles))
        .withPropertyValues(propertyValues)
        .run(assertions::accept);
  }

  @Configuration(proxyBeanMethods = false)
  static class TestDependencies {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    SdgApiClientConfigurationProperties sdgApiClientConfigurationProperties(
        org.springframework.core.env.Environment environment) {
      var properties = new SdgApiClientConfigurationProperties();
      if (!Boolean.parseBoolean(environment.getProperty("test.sdg.host.missing", "false"))) {
        properties.setHost("http://localhost:8000");
      }
      properties.setToken("test-api-key");
      return properties;
    }
  }
}
