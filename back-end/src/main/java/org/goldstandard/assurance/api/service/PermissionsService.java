package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Service;

/**
 * Initial basic service to handle permissions logic for limiting access to data by role or other
 * criteria Service should be extended to read the permissions from a file or DB table at startup
 * rather than the current hard coded permissions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionsService {

  // Roles strings as defined in Cognito Groups
  public static final String ADMIN_ROLE = "GS-Admin";
  public static final String ARM_TEAM_ROLE = "ARM-Team";
  public static final String GS_REGISTRY_ROLE = "GS-Registry";
  public static final String FINANCE_ROLE = "Finance";
  public static final String GS_REVIEWER_ROLE = "GS-Reviewer";
  public static final String PROJECT_DEVELOPER_ROLE = "Project-Developer";
  public static final String VVB_ROLE = "VVB";
  public static final String TAC_ROLE = "TAC";
  public static final String SUPPORT_ROLE = "Support";

  private final AuthenticatedUserSupplier userSupplier;

  public void validateIsAdmin() {
    if (isAdmin()) {
      return;
    }
    throw new PermissionException("User is not admin");
  }

  public boolean isAdmin() {
    return currentUserHasRole(ADMIN_ROLE);
  }

  public boolean isAdminOrArm() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, ARM_TEAM_ROLE);
  }

  public boolean canAccessAllProjects() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, ARM_TEAM_ROLE);
  }

  public boolean canAccessAllPrivateDocuments() {
    return currentUserHasAtLeastOneRole(
        ADMIN_ROLE, ARM_TEAM_ROLE, TAC_ROLE, FINANCE_ROLE, SUPPORT_ROLE);
  }

  public boolean canAccessAllUsers() {
    return isAdminOrArm();
  }

  public Account forceGetAccountForUser() {
    return userSupplier.forceGetAuthenticatedUserAccount();
  }

  public Optional<Integer> getAccountIdForUser() {
    return userSupplier.getAuthenticatedUserAccountId();
  }

  public boolean currentUserHasRole(String role) {
    return userSupplier.getAuthenticatedUserRoles().contains(role);
  }

  public boolean isAuthenticated() {
    return getAuthenticatedUser().isPresent();
  }

  public Optional<User> getAuthenticatedUser() {
    return userSupplier.getAuthenticatedUser();
  }

  public boolean canAccessUser(UUID userId) {
    return canAccessAllUsers() || userSupplier.isAuthenticatedUser(userId);
  }

  public boolean currentUserInAccount(Project project) {
    return currentUserInAccount(project.getAccountId());
  }

  public boolean currentUserInAccount(Integer accountId) {
    return getAccountIdForUser().map(id -> Objects.equals(id, accountId)).orElse(false);
  }

  public Set<Integer> getPermittedAccountTypeIds() {
    Set<String> roles = userSupplier.getAuthenticatedUserRoles();
    if (roles.contains(PROJECT_DEVELOPER_ROLE)) {
      return Set.of(VVB_ACCOUNT_TYPE_ID);
    }
    return Collections.emptySet();
  }

  public boolean isProjectOwner(Project project) {
    if (!currentUserHasRole(PROJECT_DEVELOPER_ROLE)) {
      return false;
    }
    return currentUserInAccount(project);
  }

  public boolean isProjectDeveloperOrVVB() {
    return currentUserHasAtLeastOneRole(PROJECT_DEVELOPER_ROLE, VVB_ROLE);
  }

  public boolean isProjectDeveloper() {
    return currentUserHasAtLeastOneRole(PROJECT_DEVELOPER_ROLE);
  }

  public boolean isAdminOrFinanceOrArmOrTacOrRegistry() {
    return currentUserHasAtLeastOneRole(
        ADMIN_ROLE, ARM_TEAM_ROLE, FINANCE_ROLE, TAC_ROLE, GS_REGISTRY_ROLE);
  }

  public boolean isAdminOrFinanceOrArm() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, FINANCE_ROLE, ARM_TEAM_ROLE);
  }

  public boolean isReviewerOrVVBOrRegistry() {
    return currentUserHasAtLeastOneRole(GS_REVIEWER_ROLE, VVB_ROLE, GS_REGISTRY_ROLE);
  }

  public boolean isAdminOrArmOrTac() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, ARM_TEAM_ROLE, TAC_ROLE);
  }

  public boolean isAdminOrArmOrReviewer() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, ARM_TEAM_ROLE, GS_REVIEWER_ROLE);
  }

  public boolean isAdminOrTac() {
    return currentUserHasAtLeastOneRole(ADMIN_ROLE, TAC_ROLE);
  }

  public boolean isReviewer() {
    return currentUserHasRole(GS_REVIEWER_ROLE);
  }

  public boolean isArmOrVvb() {
    return currentUserHasAtLeastOneRole(ARM_TEAM_ROLE, VVB_ROLE);
  }

  private boolean currentUserHasAtLeastOneRole(String... roles) {
    Set<String> currentUserRoles = userSupplier.getAuthenticatedUserRoles();
    return Arrays.stream(roles).anyMatch(currentUserRoles::contains);
  }

  public boolean isSupport() {
    return currentUserHasRole(SUPPORT_ROLE);
  }
}
