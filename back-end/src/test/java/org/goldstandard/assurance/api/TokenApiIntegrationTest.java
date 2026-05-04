package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

class TokenApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/accounts";

  @Test
  void testNoAuthHeaderIsUnauthorized() {
    var infoEndpointDetails = this.restTemplate.getForEntity(baseUrl + ENDPOINT_TO_TEST, Map.class);
    assertThat(infoEndpointDetails.getStatusCode().value())
        .isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void testInvalidTokenIsUnauthorized() {
    var tokenCookie =
        ResponseCookie.from("accessToken", "INVALID-TOKEN")
            .httpOnly(true)
            .secure(true)
            .path("/api")
            .maxAge(Duration.ofDays(1))
            .build();

    var headers = new HttpHeaders();
    headers.set(HttpHeaders.COOKIE, tokenCookie.toString());

    var requestEntity = new HttpEntity<>(headers);

    var infoEndpointDetails =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST, HttpMethod.GET, requestEntity, ResponseEntity.class);
    assertThat(infoEndpointDetails.getStatusCode().value())
        .isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void testValidTokenIsSuccess() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var healthEndpointDetails =
        this.restTemplate.exchange(
            baseUrl + ENDPOINT_TO_TEST, HttpMethod.GET, requestEntity, String.class);
    assertThat(healthEndpointDetails.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
  }
}
