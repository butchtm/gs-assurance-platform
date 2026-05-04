package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

class TokenApiIntegrationTest extends AbstractApiTest {

  @Autowired private ApiClient client;

  @Test
  void testNoAuthHeaderIsUnauthorized() {
    var entity = new HttpEntity<>(new HttpHeaders());

    var response = client.getProject(1, entity);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
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
    var entity = new HttpEntity<>(headers);

    var response = client.getProject(1, entity);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testValidTokenIsSuccess() {
    var response = client.getProject(1, ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
