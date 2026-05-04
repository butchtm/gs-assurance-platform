package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class StatusReportApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(
      strings = {
        PROJECT_DEVELOPER_ROLE,
        TAC_ROLE,
        GS_REVIEWER_ROLE,
        VVB_ROLE,
        FINANCE_ROLE,
        GS_REGISTRY_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsers(String role) {
    var response = client.getStatusReport(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void shouldBeAccessibleByPermittedUsers(String role) {
    var response = client.getStatusReport(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void shouldAcceptDateFilterParameters(String role) {
    var response = client.getStatusReport(role, "2024-08-01", "2024-08-31");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void shouldAcceptIsoDateTimeFormat() {
    var response =
        client.getStatusReport(ADMIN_ROLE, "2024-08-12T16:26:07.000Z", "2024-08-31T23:59:59.999Z");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        PROJECT_DEVELOPER_ROLE,
        TAC_ROLE,
        GS_REVIEWER_ROLE,
        VVB_ROLE,
        FINANCE_ROLE,
        GS_REGISTRY_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsersForExcel(String role) {
    var response = client.getStatusReportExcel(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void shouldBeAccessibleByPermittedUsersForExcel(String role) {
    var response = client.getStatusReportExcel(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isPositive();
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE})
  void shouldAcceptDateFilterParametersForExcel(String role) {
    var response = client.getStatusReportExcel(role, "2024-08-01", "2024-08-31");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isPositive();
  }

  @Test
  void shouldAcceptIsoDateTimeFormatForExcel() {
    var response =
        client.getStatusReportExcel(
            ADMIN_ROLE, "2024-08-12T16:26:07.000Z", "2024-08-31T23:59:59.999Z");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isPositive();
  }
}
