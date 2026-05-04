package org.goldstandard.assurance.api.util;

import java.util.Collection;
import java.util.function.Predicate;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.stereotype.Component;

@Component
public class RequestStatusChecker {

  public boolean movedBackToInProgressOrInAuditFromCompletionCheck(
      RequestStatus fromStatus, RequestStatus toStatus) {
    if (!fromStatus.isInCompletenessCheck()) {
      return false;
    }
    return toStatus.isInPreparation() || toStatus.isInAudit();
  }

  public boolean movedBackToInProgressOrInAssignmentFromRoundCompletion(
      RequestStatus fromStatus, RequestStatus toStatus) {
    if (!fromStatus.isInRoundCompletion()) {
      return false;
    }
    return toStatus.isInAssignment() || toStatus.isInProgress();
  }

  public boolean movedToInRoundCompletionFromInAssignmentOrInProgress(
      RequestStatus fromStatus, RequestStatus toStatus) {
    if (!fromStatus.isInAssignment() || !fromStatus.isInProgress()) {
      return false;
    }
    return toStatus.isInRoundCompletion();
  }

  public boolean movedToInAssignmentFromCompletenessCheck(Request original, Request updated) {
    return movedToInAssignmentFromCompletenessCheck(
        original.getCurrentRequestStatus(), updated.getCurrentRequestStatus());
  }

  public boolean movedToInAssignmentFromCompletenessCheck(
      RequestStatus fromStatus, RequestStatus toStatus) {
    if (!fromStatus.isInCompletenessCheck()) {
      return false;
    }
    return toStatus.isInAssignment();
  }

  public boolean hasMovedFromStatus(
      RequestStatus fromStatus, RequestStatus toStatus, Predicate<RequestStatus> hasStatus) {
    return hasStatus.test(fromStatus) && !hasStatus.test(toStatus);
  }

  public boolean hasMovedToStatus(
      RequestStatus fromStatus,
      RequestStatus toStatus,
      Collection<Predicate<RequestStatus>> statuses) {
    return statuses.stream().anyMatch(status -> hasMovedToStatus(fromStatus, toStatus, status));
  }

  private boolean hasMovedToStatus(
      RequestStatus fromStatus, RequestStatus toStatus, Predicate<RequestStatus> hasStatus) {
    return !hasStatus.test(fromStatus) && hasStatus.test(toStatus);
  }
}
