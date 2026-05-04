package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class ActuatorApiIntegrationTest extends AbstractApiTest {

  @Test
  void healthEndpointShouldBeAvailable() {
    var url = String.format("%s/actuator/health", baseUrl);

    var response = restTemplate.getForObject(url, String.class);

    assertThat(response).contains("UP");
  }

  @Test
  void infoEndpointShouldBeAvailable() {
    var url = String.format("%s/actuator/info", baseUrl);

    var response = restTemplate.getForObject(url, String.class);

    assertThat(response).contains("git");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "auditevents",
        "beans",
        "caches",
        "conditions",
        "configprops",
        "env",
        "flyway",
        "httptrace",
        "integrationgraph",
        "loggers",
        "liquibase",
        "metrics",
        "mappings",
        "scheduledtasks",
        "sessions",
        "shutdown",
        "threaddump"
      })
  void allOtherActuatorEndpointsShouldNotBeAvailable(String endpoint) {
    var url = String.format("%s/actuator/%s", baseUrl, endpoint);
    var entity = getHttpEntityWithToken();

    var response = restTemplate.exchange(url, GET, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
