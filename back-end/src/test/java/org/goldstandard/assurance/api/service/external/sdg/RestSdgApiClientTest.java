package org.goldstandard.assurance.api.service.external.sdg;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
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
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriodMethodology;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
  void shouldPostExpectedVpaProjectPayloadToSdg() {
    var project = buildSdgProject();
    wireMock.stubFor(
        buildExpectedRequest(project.getGsid())
            .willReturn(aResponse().withStatus(200).withBody("any response body")));
    var client = buildApiClient();

    ThrowingCallable call = () -> client.updateProject(project);

    assertThatCode(call).doesNotThrowAnyException();
    wireMock.verify(
        1,
        postRequestedFor(urlEqualTo("/api/projects/GS23982/"))
            .withHeader("Content-Length", matching("[1-9][0-9]*"))
            .withoutHeader("Transfer-Encoding"));
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
        .restTemplate(
            new RestTemplate(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())))
        .build();
  }

  private String buildMockServerHost() {
    return String.format("http://localhost:%d", wireMock.getPort());
  }

  private static SdgProject buildSdgProject() {
    var creditingPeriod =
        ExternalCreditingPeriod.builder()
            .id(1)
            .startDate(ZonedDateTimeMother.withDateOnly(2022, Month.JULY, 7))
            .endDate(ZonedDateTimeMother.withDateOnly(2027, Month.JULY, 8))
            .methodologies(
                List.of(
                    ExternalCreditingPeriodMethodology.builder()
                        .id(62)
                        .description(
                            "AM0091 Energy efficiency technologies and fuel switching in new buildings")
                        .version(1.0)
                        .build()))
            .build();
    return SdgProject.builder()
        .id(23982)
        .gsid("GS23982")
        .poaProjectId(23980)
        .name("GS23980-GS23982-My Regular VPA for testing")
        .description("My Regular VPA for my Real VPA for my Test POA Updated Description")
        .status("NEW_PROJECT")
        .countryCodes(List.of("AF"))
        .annualEstimatedCredits(50000)
        .carbonStream("GS_VER")
        .types(List.of("PV"))
        .projectDeveloper("TestPD")
        .programOfActivities("VPA")
        .creditingPeriods(List.of(creditingPeriod))
        .build();
  }

  private static MappingBuilder buildExpectedRequest(String gsid) {
    return post(urlEqualTo(String.format("/api/projects/%s/", gsid)))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("Authorization", equalTo(String.format("Api-Key %s", API_KEY)))
        .withHeader("Content-Length", matching("[1-9][0-9]*"))
        .withRequestBody(
            equalToJson(loadContentFromClasspath("json/expected-sdg-project-request-body.json")));
  }
}
