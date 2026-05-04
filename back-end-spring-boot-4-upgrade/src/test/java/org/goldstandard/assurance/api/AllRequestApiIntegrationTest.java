package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.UserPopulator.ANY_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.populator.RequestPopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AllRequestApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @ParameterizedTest
  @ValueSource(
      strings = {
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE
      })
  void shouldReturnForbiddenForNonPermittedUsers(String role) {
    var response = client.getAllRequestsForUser(role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {ARM_TEAM_ROLE, ADMIN_ROLE})
  void shouldBeAccessibleByPermittedUsers(String role) {
    var response = client.getAllRequestsForUser(role, ANY_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void shouldReturnRequests() {
    var response = client.getAllRequests();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var allRequests = response.getBody().getContent();
    assertThat(allRequests).hasSize(RequestPopulator.TEST_REQUESTS.size());
  }

  @ParameterizedTest
  @MethodSource("filtersAndExpectedRequestCounts")
  void shouldFilterAssignedRequests(String filter, int expectedCount) {
    var response = client.getAllRequestsWithQuery(filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(expectedCount);
  }

  private static Stream<Arguments> filtersAndExpectedRequestCounts() {
    return Stream.of(
        Arguments.of("requestStatusIds=10", 3),
        Arguments.of("projectIds=1", 2),
        Arguments.of("projectAccountIds=1", 2),
        Arguments.of("reviewerAccountIds=1", 0),
        Arguments.of("vvbAccountIds=1", 1),
        Arguments.of("invoiceStatusIds=2", 6),
        Arguments.of("activityIds=1", 1),
        Arguments.of("responsibleAccountIds=1", 2),
        Arguments.of("requestTypeIds=1000", 3),
        Arguments.of("projectSearch=GS1002", 2),
        Arguments.of("projectSearch=PROJECT 2", 2),
        Arguments.of("qaqcAccountIds=1", 1));
  }
}
