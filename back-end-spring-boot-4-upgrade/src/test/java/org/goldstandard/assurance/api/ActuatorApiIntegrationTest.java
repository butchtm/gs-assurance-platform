package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class ActuatorApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Test
  void healthEndpointShouldBeAvailable() {
    var response = client.getActuatorHealth();

    var body = response.getBody();
    assertThat(body).isNotNull().contains("UP");
  }

  @Test
  void infoEndpointShouldBeAvailable() {
    var response = client.getActuatorInfo();

    var body = response.getBody();
    assertThat(body).isNotNull().contains("git");
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

    var response = client.getActuator(endpoint);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
