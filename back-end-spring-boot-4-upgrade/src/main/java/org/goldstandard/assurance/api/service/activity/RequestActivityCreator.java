package org.goldstandard.assurance.api.service.activity;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestActivityCreator {

  private final Clock clock;
  private final AssignmentFinder assignmentFinder;
  private final AccountFinder accountFinder;
  private final ActivityDeadlineCalculator activityDeadlineCalculator;
  private final RequestActivityDisplayNameFactory displayNameFactory;
  private final AuthenticatedUserSupplier userSupplier;

  public RequestActivity buildRequestActivity(Activity activity, Request request) {
    return buildRequestActivity(activity, request, ZonedDateTime.now(clock));
  }

  public RequestActivity buildSkippedRequestActivity(
      Activity activity, Request request, ZonedDateTime date) {
    return toRequestActivityBuilder(activity, request, date)
        .responsibleAccount(
            userSupplier.getAuthenticatedUserAccount().orElseGet(accountFinder::getArmTeamAccount))
        .skipped(true)
        .endDate(date)
        .build();
  }

  private RequestActivity buildRequestActivity(
      Activity activity, Request request, ZonedDateTime startDate) {
    return toRequestActivityBuilder(activity, request, startDate)
        .responsibleAccount(toResponsibleAccount(request, activity))
        .skipped(false)
        .build();
  }

  private RequestActivity.RequestActivityBuilder toRequestActivityBuilder(
      Activity activity, Request request, ZonedDateTime startDate) {
    var deadline =
        activityDeadlineCalculator.calculateDeadline(startDate, activity, request.getId());
    return RequestActivity.builder()
        .requestId(request.getId())
        .activity(activity)
        .startDate(startDate)
        .plannedEndDate(deadline)
        .displayName(displayNameFactory.toDisplayName(request, activity));
  }

  private Account toResponsibleAccount(Request request, Activity activity) {
    if (activity.getAccountType().isReviewer()) {
      return assignmentFinder.forceFindLatestAccountAssignedToReviewRequest(request.getId());
    }
    if (activity.getAccountType().isArm()) {
      return assignmentFinder
          .findLatestAccountAssignedToQAQCRequest(request.getId())
          .orElseGet(accountFinder::getArmTeamAccount);
    }
    return accountFinder.getArmTeamAccount();
  }
}
