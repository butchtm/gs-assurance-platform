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

class RegistryUpdateApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Test
  void adminsShouldBePermittedToPublishProjectToRegistry() {
    var projectId = 1;

    var response = client.publishRegistryUpdate(ADMIN_ROLE, projectId);

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

    var response = client.publishRegistryUpdate(role, projectId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void adminsShouldBePermittedToPublishAllRegistryUpdates() {
    var response = client.publishAllRegistryUpdates(ADMIN_ROLE);

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
    var response = client.publishAllRegistryUpdates(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
