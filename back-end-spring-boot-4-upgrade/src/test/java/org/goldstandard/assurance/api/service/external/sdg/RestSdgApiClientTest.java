package org.goldstandard.assurance.api.service.external.sdg;

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
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.fixtures.ExternalCreditingPeriodMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RestSdgApiClientTest {

  private static final String API_KEY = "mock-api-key";

  @RegisterExtension
  static WireMockExtension wireMock =
      WireMockExtension.newInstance()
          .options(WireMockConfiguration.options().dynamicPort())
          .build();

  @Test
  void shouldNotThrowExceptionIfCallToSdgCompletesSuccessfully() {
    var project = buildSdgProject();
    wireMock.stubFor(
        buildExpectedRequest(project.getGsid())
            .willReturn(aResponse().withStatus(200).withBody("any response body")));
    var client = buildApiClient();

    ThrowingCallable call = () -> client.updateProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldThrowExceptionIfCallToSdgDoesNotCompleteSuccessfully() {
    var errorResponseBody = "errorResponseBody";
    var project = buildSdgProject();
    wireMock.stubFor(
        buildExpectedRequest(project.getGsid())
            .willReturn(aResponse().withStatus(400).withBody(errorResponseBody)));
    var client = buildApiClient();

    Throwable error = catchThrowable(() -> client.updateProject(project));

    assertThat(error)
        .isInstanceOf(SdgApiClientException.class)
        .hasCauseInstanceOf(RestClientException.class)
        .hasMessageContaining(errorResponseBody);
  }

  private RestSdgApiClient buildApiClient() {
    return RestSdgApiClient.builder()
        .host(buildMockServerHost())
        .token(API_KEY)
        .restTemplate(new RestTemplate())
        .build();
  }

  private String buildMockServerHost() {
    return String.format("http://localhost:%d", wireMock.getPort());
  }

  private static SdgProject buildSdgProject() {
    return SdgProject.builder()
        .id(1)
        .gsid("GS1234")
        .name("test project name")
        .description("test project description")
        .status("NEW_PROJECT")
        .countryCodes(List.of("XZ", "GB"))
        .annualEstimatedCredits(987)
        .carbonStream("GS_CER")
        .types(List.of("A_R", "BIOGAS_ELECTRICITY"))
        .projectDeveloper("test account name")
        .programOfActivities("POA")
        .creditingPeriods(ExternalCreditingPeriodMother.buildCreditingPeriods())
        .build();
  }

  private static MappingBuilder buildExpectedRequest(String gsid) {
    return post(urlEqualTo(String.format("/api/projects/%s/", gsid)))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("Authorization", equalTo(String.format("Api-Key %s", API_KEY)))
        .withRequestBody(
            equalToJson(loadContentFromClasspath("json/expected-sdg-project-request-body.json")));
  }
}
