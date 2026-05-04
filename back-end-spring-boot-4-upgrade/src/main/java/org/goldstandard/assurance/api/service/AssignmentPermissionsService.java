package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.AssignmentStatus.ALL;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentStatus.PENDING;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestAccountAssignment;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class AssignmentPermissionsService {

  private static final Map<String, Set<Integer>> ROLE_PERMITTED_STATUSES =
      Map.of(
          GS_REVIEWER_ROLE, Set.of(PENDING),
          GS_REGISTRY_ROLE, Set.of(PENDING),
          FINANCE_ROLE, ALL,
          VVB_ROLE, Set.of(PENDING));
  private static final Set<String> UNRESTRICTED_ASSIGNERS = Set.of(ADMIN_ROLE, ARM_TEAM_ROLE);
  private static final Set<String> UNRESTRICTED_READERS =
      Set.of(ADMIN_ROLE, ARM_TEAM_ROLE, GS_REGISTRY_ROLE, FINANCE_ROLE);
  public static final Set<String> ASSIGNED_READERS = Set.of(GS_REVIEWER_ROLE, VVB_ROLE);

  private final AuthenticatedUserSupplier userSupplier;
  private final RequestRepository requestRepository;
  private final AccountFinder accountFinder;
  private final AssignmentFinder assignmentFinder;

  public void validateCanUpdateStatus(RequestAccountAssignment assignment) {
    if (canUpdateStatus(assignment)) {
      return;
    }
    throw new PermissionException(
        "Not permitted to update status for assignment %s", assignment.getId());
  }

  private boolean canUpdateStatus(RequestAccountAssignment assignment) {
    return userSupplier.isAuthenticatedUserAssignedToAccount(assignment.getAccount().getId())
        && userSupplier.getAuthenticatedUserRoles().stream()
            .anyMatch(
                r ->
                    ROLE_PERMITTED_STATUSES.containsKey(r)
                        && ROLE_PERMITTED_STATUSES
                            .get(r)
                            .contains(assignment.getAssignmentStatus().getId()));
  }

  public void validateCanReadRequestAssignments(Integer requestId) {
    if (canReadAnyAssignment()) {
      return;
    }
    if (projectOwner(requestId)) {
      return;
    }
    if (canReadOwnAccountAssignments() && userSupplier.getAuthenticatedUserAccount().isPresent()) {
      return;
    }
    throw new PermissionException("Not permitted to view assignments for request %s", requestId);
  }

  public void validateCanReadAssignment(RequestAccountAssignment assignment) {
    if (canReadAnyAssignment()) {
      return;
    }
    if (canReadOwnAccountAssignment(assignment)) {
      return;
    }
    if (projectOwnerAndVVBAssignment(assignment)) {
      return;
    }
    throw new PermissionException("Not permitted to view assignment %s", assignment.getId());
  }

  public void validateCanReadAccountAssignments(Integer accountId) {
    if (canReadAnyAssignment()) {
      return;
    }
    if (canReadOwnAccountAssignments(accountId)) {
      return;
    }
    throw new PermissionException("Not permitted to read assignments for account {}", accountId);
  }

  public boolean canReadAnyAssignment() {
    return userSupplier.getAuthenticatedUserRoles().stream()
        .anyMatch(UNRESTRICTED_READERS::contains);
  }

  public boolean canReadOwnAccountAssignment(RequestAccountAssignment assignment) {
    return canReadOwnAccountAssignments(assignment.getAccount().getId());
  }

  public boolean canReadOwnAccountAssignments(Integer accountId) {
    return canReadOwnAccountAssignments()
        && userSupplier.isAuthenticatedUserAssignedToAccount(accountId);
  }

  public boolean canReadOwnAccountAssignments() {
    return userSupplier.getAuthenticatedUserRoles().stream().anyMatch(ASSIGNED_READERS::contains);
  }

  public boolean projectOwner(Integer requestId) {
    // Only Project Developers should be Project owners.
    if (!userSupplier.getAuthenticatedUserRoles().contains(PROJECT_DEVELOPER_ROLE)) {
      return false;
    }
    var projectAccountIds = requestRepository.getProjectAccountIdsForRequestId(requestId);
    if (CollectionUtils.isEmpty(projectAccountIds)) {
      throw new DataException("Project not found for request id %d", requestId);
    }
    return userSupplier.isAuthenticatedUserAssignedToAnyAccounts(projectAccountIds);
  }

  public boolean isRequestAssignedToUserAccount(Integer requestId) {
    return userSupplier
        .getAuthenticatedUserAccountId()
        .map(accountId -> assignmentFinder.isRequestAssignedToAccount(requestId, accountId))
        .orElse(false);
  }

  public boolean projectOwnerAndVVBAssignment(RequestAccountAssignment assignment) {
    return projectOwner(assignment.getRequestId())
        && accountFinder.isVVBAccount(assignment.getAccount().getId());
  }

  public void validateCanAssignToRequest(Request request) {
    if (!canAssignToRequest(request)) {
      throw new PermissionException("User cannot assign to request with id %s", request.getId());
    }
  }

  public boolean hasActiveRequestsAssignedToUserAccount(Project project) {
    Optional<Integer> accountId = userSupplier.getAuthenticatedUserAccountId();
    log.debug("got account id {} for current user", accountId);
    return accountId
        .map(id -> hasActiveRequestsAssignedToUserAccount(project, accountId.get()))
        .orElse(false);
  }

  private boolean hasActiveRequestsAssignedToUserAccount(Project project, Integer accountId) {
    return requestRepository.findByProjectId(project.getId()).stream()
        .anyMatch(request -> hasActiveStatusAndAssignedToAccount(request, accountId));
  }

  private boolean hasActiveStatusAndAssignedToAccount(Request request, Integer accountId) {
    var parentRequest = request.getParentIfChild();
    return !parentRequest.hasInactiveCurrentStatus()
        && assignmentFinder.isRequestAssignedToAccount(parentRequest.getId(), accountId);
  }

  private boolean canAssignToRequest(Request request) {
    if (canAssignToAnyRequest()) {
      return true;
    }
    var projectAccountId = request.getProject().getAccountId();
    return userSupplier.isAuthenticatedUserAssignedToAccount(projectAccountId);
  }

  private boolean canAssignToAnyRequest() {
    return userSupplier.getAuthenticatedUserRoles().stream()
        .anyMatch(UNRESTRICTED_ASSIGNERS::contains);
  }
}
