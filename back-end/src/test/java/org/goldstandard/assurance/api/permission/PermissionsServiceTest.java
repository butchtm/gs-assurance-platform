package org.goldstandard.assurance.api.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionsServiceTest {

  @InjectMocks PermissionsService permissionsService;

  @Mock AuthenticatedUserSupplier userSupplier;

  @Test
  void adminCanAccessAllProjects() {
    givenAuthenticatedUserWithRole(ADMIN_ROLE);

    boolean canAccess = permissionsService.canAccessAllProjects();

    assertThat(canAccess).isTrue();
  }

  @ParameterizedTest
  @MethodSource("nonAdminOrArmRoles")
  void nonAdminOrArmCannotAccessAllProjects(String role) {
    givenAuthenticatedUserWithRole(role);

    boolean canAccess = permissionsService.canAccessAllProjects();

    assertThat(canAccess).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {ADMIN_ROLE, ARM_TEAM_ROLE, TAC_ROLE, FINANCE_ROLE})
  void permittedRolesCanViewPrivateDocuments(String role) {
    givenAuthenticatedUserWithRole(role);

    boolean canAccess = permissionsService.canAccessAllPrivateDocuments();

    assertThat(canAccess).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {GS_REGISTRY_ROLE, GS_REVIEWER_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  void nonPermittedRolesCannotViewPrivateDocuments(String role) {
    givenAuthenticatedUserWithRole(role);

    boolean canAccess = permissionsService.canAccessAllPrivateDocuments();

    assertThat(canAccess).isFalse();
  }

  @Test
  void shouldReturnTrueIfUserIsReviewer() {
    givenAuthenticatedUserWithRole(GS_REVIEWER_ROLE);

    boolean reviewerOrVvb = permissionsService.isReviewerOrVVBOrRegistry();

    assertThat(reviewerOrVvb).isTrue();
  }

  @Test
  void shouldReturnTrueIfUserIsVVB() {
    givenAuthenticatedUserWithRole(VVB_ROLE);

    boolean reviewerOrVvb = permissionsService.isReviewerOrVVBOrRegistry();

    assertThat(reviewerOrVvb).isTrue();
  }

  @Test
  void shouldReturnTrueIfUserIsRegistry() {
    givenAuthenticatedUserWithRole(GS_REGISTRY_ROLE);

    boolean reviewerOrVvb = permissionsService.isReviewerOrVVBOrRegistry();

    assertThat(reviewerOrVvb).isTrue();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE, PROJECT_DEVELOPER_ROLE, TAC_ROLE})
  void shouldReturnFalseIfUserIsNotReviewerOrVVBOrRegistry(String role) {
    givenAuthenticatedUserWithRole(role);

    boolean reviewerOrVvb = permissionsService.isReviewerOrVVBOrRegistry();

    assertThat(reviewerOrVvb).isFalse();
  }

  @Test
  void armCanAccessAllProjects() {
    givenAuthenticatedUserWithRole(ARM_TEAM_ROLE);

    boolean canAccess = permissionsService.canAccessAllProjects();

    assertThat(canAccess).isTrue();
  }

  private void givenAuthenticatedUserWithRole(String role) {
    when(userSupplier.getAuthenticatedUserRoles()).thenReturn(Set.of(role));
  }

  private static Stream<String> nonAdminOrArmRoles() {
    return Stream.of(
        GS_REGISTRY_ROLE,
        FINANCE_ROLE,
        GS_REVIEWER_ROLE,
        PROJECT_DEVELOPER_ROLE,
        VVB_ROLE,
        TAC_ROLE);
  }
}
