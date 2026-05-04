package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID2;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import org.goldstandard.assurance.api.populator.UserPopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UserApiIntegrationTest extends AbstractApiTest {

  @Autowired ApiClient client;

  @Autowired UserPopulator userPopulator;

  @Test
  void testGetUsers() {
    var response = client.getAllUsers(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var users = response.getBody();
    assertThat(users).hasSize(userPopulator.getNumberOfUsers());
    var user = users.getFirst();
    assertThat(user.getId()).isEqualTo(1);
    assertThat(user.getUserName()).isEqualTo("User 1");
    assertThat(user.getEmailAddress()).isEqualTo("user_1@test");
    assertThat(user.getIsActive()).isFalse();
  }

  @Test
  void testGetPaginatedUsers() {
    var response = client.getUserPage(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var page = response.getBody();
    assertThat(page).isNotNull();
    var users = page.getContent();
    assertThat(users).hasSize(userPopulator.getNumberOfUsers());

    var user = users.getFirst();
    assertThat(user.getId()).isEqualTo(1);
    assertThat(user.getUserName()).isEqualTo("User 1");
    assertThat(user.getEmailAddress()).isEqualTo("user_1@test");
    assertThat(user.getIsActive()).isFalse();
  }

  @Test
  void testGetActiveUsers() {
    var response = client.getAllActiveUsers(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var users = response.getBody();
    assertThat(users).hasSize(userPopulator.getNumberOfActiveUsers());
    var user = users.getFirst();
    assertThat(user.getId()).isEqualTo(2);
    assertThat(user.getUserName()).isEqualTo("User 2");
    assertThat(user.getEmailAddress()).isEqualTo("user_2@test");
    assertThat(user.getIsActive()).isTrue();
  }

  @Test
  void testSearchUsersByUsername() {
    var username = "User 1";

    var response = client.searchUsers(username, ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var users = response.getBody();
    assertThat(users).hasSize(1);
    var user = users.getFirst();
    assertThat(user.getId()).isEqualTo(1);
    assertThat(user.getUserName()).isEqualTo(username);
    assertThat(user.getEmailAddress()).isEqualTo("user_1@test");
    assertThat(user.getIsActive()).isFalse();
  }

  @Test
  void testSearchUsersByEmailAddress() {
    var emailAddress = "user_1@test";

    var response = client.searchUsers(emailAddress, ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var users = response.getBody();
    assertThat(users).hasSize(1);
    var user = users.getFirst();
    assertThat(user.getId()).isEqualTo(1);
    assertThat(user.getUserName()).isEqualTo("User 1");
    assertThat(user.getEmailAddress()).isEqualTo(emailAddress);
    assertThat(user.getIsActive()).isFalse();
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
  void testGetUsersNotAdminForbidden(String role) {
    var response = client.getAllUsers(role);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetUsersUnauthenticatedIsDenied() {
    var response = client.getAllUsersUnauthenticated();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
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
  void testGetUserNonAdminUserSameUserSucceeds(String role) {
    var userId = NO_ACCOUNT_USER_ID1;

    var response = client.getUser(userId, role, userId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

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
  void testGetUserNonAdminUserOtherUserReturnsNotFound(String role) {
    var response = client.getUser(NO_ACCOUNT_USER_ID1, role, NO_ACCOUNT_USER_ID2);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        ADMIN_ROLE,
        ARM_TEAM_ROLE,
      })
  void testGetUserOtherUserReturnsSucceeds(String role) {
    var response = client.getUser(NO_ACCOUNT_USER_ID1, role, NO_ACCOUNT_USER_ID2);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
