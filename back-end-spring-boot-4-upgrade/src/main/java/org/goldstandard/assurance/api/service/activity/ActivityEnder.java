package org.goldstandard.assurance.api.service.activity;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ActivityEnder {

  private final Clock clock;

  public Request endCurrentActivityIfRequired(Request originalRequest, Request updatedRequest) {
    return updatedRequest
        .getCurrentActivity()
        .map(activity -> endCurrentActivityIfRequired(activity, originalRequest, updatedRequest))
        .orElse(updatedRequest);
  }

  private Request endCurrentActivityIfRequired(
      RequestActivity activity, Request originalRequest, Request updatedRequest) {
    if (shouldEndActivity(originalRequest, updatedRequest)) {
      return endCurrentActivity(activity, updatedRequest);
    }
    return updatedRequest;
  }

  private Request endCurrentActivity(RequestActivity activity, Request updatedRequest) {
    return updatedRequest.withCurrentActivity(activity.withEndDate(ZonedDateTime.now(clock)));
  }

  private static boolean shouldEndActivity(Request original, Request updated) {
    if (updated.isCurrentActivityComplete()) {
      return false;
    }
    if (isHarmonisationLoop(original, updated)) {
      return false;
    }
    return isRequestStatusChanged(original, updated);
  }

  private static boolean isRequestStatusChanged(Request original, Request updated) {
    return !Objects.equals(
        original.getCurrentRequestStatus().getId(), updated.getCurrentRequestStatus().getId());
  }

  private static boolean isHarmonisationLoop(Request original, Request updated) {
    if (!original.isCurrentActivityHarmonisationOfFeedback()) {
      return false;
    }

    if (original.statusIsInRoundCompletion() && updated.statusIsInProgress()) {
      return true;
    }

    return original.statusIsInProgress() && updated.statusIsInRoundCompletion();
  }
}
