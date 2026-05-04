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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class RegistryUpdateApiIntegrationTest extends AbstractApiTest {

  @Test
  void adminsShouldBePermittedToPublishProjectToRegistry() {
    var projectId = 1;

    var response =
        restTemplate.exchange(
            String.format("%s/registry-updates/projects/%d", baseUrl, projectId),
            HttpMethod.POST,
            getHttpEntityWithTokenForRole(ADMIN_ROLE),
            Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void nonAdminsShouldNotBePermittedToPublishProjectToRegistry(String role) {
    var projectId = 2;

    var response =
        restTemplate.exchange(
            String.format("%s/registry-updates/projects/%d", baseUrl, projectId),
            HttpMethod.POST,
            getHttpEntityWithTokenForRole(role),
            Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void adminsShouldBePermittedToPublishAllRegistryUpdates() {
    var response =
        restTemplate.exchange(
            String.format("%s/registry-updates/projects", baseUrl),
            HttpMethod.POST,
            getHttpEntityWithTokenForRole(ADMIN_ROLE),
            Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ARM_TEAM_ROLE,
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void nonAdminsShouldNotBePermittedToPublishAllRegistryUpdates(String role) {
    var response =
        restTemplate.exchange(
            String.format("%s/registry-updates/projects", baseUrl),
            HttpMethod.POST,
            getHttpEntityWithTokenForRole(role),
            Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
