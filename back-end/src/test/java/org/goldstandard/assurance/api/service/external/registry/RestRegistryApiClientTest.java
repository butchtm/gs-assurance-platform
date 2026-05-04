package org.goldstandard.assurance.api.service.external.registry;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Month;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.fixtures.ExternalCreditingPeriodMother;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RestRegistryApiClientTest {

  private static final String AUTH_TOKEN = "mock-auth-token";

  @RegisterExtension
  static WireMockExtension wireMock =
      WireMockExtension.newInstance()
          .options(WireMockConfiguration.options().dynamicPort())
          .build();

  @Test
  void shouldNotThrowExceptionIfCallToRegistryCompletesSuccessfully() {
    wireMock.stubFor(
        buildExpectedRequest()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"any\": \"response body\"}")));
    var client = buildApiClient();
    var project = buildRegistryProject();

    ThrowingCallable call = () -> client.updateProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldReturnRegistryProjectIdIfCallToRegistryCompletesSuccessfully() {
    wireMock.stubFor(
        buildExpectedRequest()
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        loadContentFromClasspath("json/registry-project-response-body.json"))));
    var client = buildApiClient();
    var project = buildRegistryProject();

    var registryId = client.updateProject(project);

    assertThat(registryId).contains(1666);
  }

  @Test
  void shouldThrowExceptionIfCallToRegistryDoesNotCompleteSuccessfully() {
    var errorResponseBody = "{\"error\": \"response body\"}";
    wireMock.stubFor(
        buildExpectedRequest()
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody(errorResponseBody)));
    var client = buildApiClient();
    var project = buildRegistryProject();

    Throwable error = catchThrowable(() -> client.updateProject(project));

    assertThat(error)
        .isInstanceOf(RegistryApiClientException.class)
        .hasCauseInstanceOf(RestClientException.class)
        .hasMessageContaining(errorResponseBody);
  }

  private RestRegistryApiClient buildApiClient() {
    return RestRegistryApiClient.builder()
        .host(buildMockServerHost())
        .token(AUTH_TOKEN)
        .restTemplate(new RestTemplate())
        .build();
  }

  private String buildMockServerHost() {
    return String.format("http://localhost:%d", wireMock.getPort());
  }

  private static RegistryProject buildRegistryProject() {
    return RegistryProject.builder()
        .id(1)
        .projectId(1234)
        .poaId(null)
        .gsidNum(1234)
        .name("test project name")
        .description("test project description")
        .status("NEW_PROJECT")
        .standardsVersion("Gold Standard for the Global Goals")
        .countryCode("XZ")
        .estimatedAnnualCredits(987)
        .creditingPeriodStartDate(ZonedDateTimeMother.withDateOnly(2024, Month.JANUARY, 1))
        .creditingPeriodEndDate(ZonedDateTimeMother.withDateOnly(2028, Month.JANUARY, 1))
        .methodology("ACM0001 Flaring or use of landfill gas")
        .carbonStream("GS_CER")
        .type("A_R")
        .size("Large Scale")
        .url("http://localhost:3001/projects/GS1234")
        .sustainableDevelopmentGoals(List.of("AFFORDABLE_AND_CLEAN_ENERGY", "ZERO_HUNGER"))
        .projectDeveloper("test account name")
        .programOfActivities("POA")
        .creditingPeriods(ExternalCreditingPeriodMother.buildCreditingPeriods())
        .designCertificationDate(ZonedDateTimeMother.withDateOnly(2025, Month.JANUARY, 1))
        .build();
  }

  private static MappingBuilder buildExpectedRequest() {
    return post(urlEqualTo("/sustaincert/projects"))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("X-SustainCERT-Auth-Token", equalTo(AUTH_TOKEN))
        .withRequestBody(
            equalToJson(
                loadContentFromClasspath("json/expected-registry-project-request-body.json")));
  }
}
