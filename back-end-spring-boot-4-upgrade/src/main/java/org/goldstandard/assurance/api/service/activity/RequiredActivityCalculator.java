package org.goldstandard.assurance.api.service.activity;

import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.stereotype.Component;

@Component
public class RequiredActivityCalculator {

  public enum RequiredActivity {
    FIRST_FROM_PATHWAY,
    FIRST_FROM_PATHWAY_GROUP,
    NEXT_ACTIVITY,
    SKIP_NEXT_ACTIVITY,
    NONE
  }

  public RequiredActivity calculateRequiredActivity(Request request, RequestStatus originalStatus) {
    if (shouldStartNextPathway(request)) {
      return RequiredActivity.FIRST_FROM_PATHWAY;
    }
    if (shouldStartNextPathwayGroup(request)) {
      return RequiredActivity.FIRST_FROM_PATHWAY_GROUP;
    }
    if (shouldStartNextActivity(request, originalStatus)) {
      return RequiredActivity.NEXT_ACTIVITY;
    }
    if (shouldSkipNextActivity(request, originalStatus)) {
      return RequiredActivity.SKIP_NEXT_ACTIVITY;
    }

    return RequiredActivity.NONE;
  }

  private boolean shouldStartNextPathway(Request request) {
    var updatedStatus = request.getCurrentRequestStatus();
    if (!request.hasCurrentActivity() && updatedStatus.isInCompletenessCheck()) {
      return true;
    }
    return updatedStatus.isInAssignment();
  }

  private boolean shouldStartNextPathwayGroup(Request request) {
    if (!request.hasCurrentActivity()) {
      return false;
    }
    var updatedStatus = request.getCurrentRequestStatus();
    if (updatedStatus.isInCompletenessCheck()) {
      return true;
    }
    var requestRounds = request.getRequestRounds().size();
    if (updatedStatus.isInProgress() && requestRounds > 1) {
      return true;
    }
    return updatedStatus.isInRoundCompletion() && requestRounds > 1 && request.isFastTracked();
  }

  private boolean shouldStartNextActivity(Request request, RequestStatus originalStatus) {
    if (!request.hasCurrentActivity()) {
      return false;
    }
    var updatedStatus = request.getCurrentRequestStatus();
    var requestRounds = request.getRequestRounds().size();

    if (updatedStatus.isInProgress() && requestRounds == 1) {
      return !request.isCurrentActivityHarmonisationOfFeedback();
    }

    if (updatedStatus.isInRoundCompletion()
        && originalStatus.isInAssignment()
        && !request.isFastTracked()) {
      return false;
    }

    if (updatedStatus.isInRoundCompletion() && requestRounds == 1) {
      return true;
    }
    return updatedStatus.isInRoundCompletion() && requestRounds > 1 && !request.isFastTracked();
  }

  private boolean shouldSkipNextActivity(Request request, RequestStatus originalStatus) {
    if (!request.hasCurrentActivity()) {
      return false;
    }
    var updatedStatus = request.getCurrentRequestStatus();

    return originalStatus.isInAssignment() && updatedStatus.isInRoundCompletion();
  }
}
