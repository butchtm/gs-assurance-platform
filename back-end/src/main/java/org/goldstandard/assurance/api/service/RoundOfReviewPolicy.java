package org.goldstandard.assurance.api.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestRoundStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.util.RequestStatusChecker;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoundOfReviewPolicy {

  private final RequestStatusChecker statusChecker;

  public boolean shouldStartRoundOfReview(Request original, Request updated) {
    if (statusChecker.movedBackToInProgressOrInAssignmentFromRoundCompletion(
        original.getCurrentRequestStatus(), updated.getCurrentRequestStatus())) {
      return false;
    }

    if (statusChecker.movedToInRoundCompletionFromInAssignmentOrInProgress(
        original.getCurrentRequestStatus(), updated.getCurrentRequestStatus())) {
      return false;
    }

    if (statusChecker.hasMovedToStatus(
        original.getCurrentRequestStatus(),
        updated.getCurrentRequestStatus(),
        List.of(
            RequestStatus::isInAssignment,
            RequestStatus::isInProgress,
            RequestStatus::isInRoundCompletion))) {
      return !hasAnyOpenRounds(updated);
    }
    return false;
  }

  public boolean shouldCloseRoundOfReview(Request original, Request updated) {
    if (statusChecker.movedBackToInProgressOrInAssignmentFromRoundCompletion(
        original.getCurrentRequestStatus(), updated.getCurrentRequestStatus())) {
      return false;
    }

    if (statusChecker.hasMovedFromStatus(
        original.getCurrentRequestStatus(),
        updated.getCurrentRequestStatus(),
        RequestStatus::isInRoundCompletion)) {
      return hasAnyOpenRounds(updated);
    }
    return false;
  }

  private boolean hasAnyOpenRounds(Request request) {
    return request.getRequestRounds().stream()
        .anyMatch(
            requestRound ->
                Objects.equals(requestRound.getStatus().getId(), RequestRoundStatus.OPEN));
  }
}
