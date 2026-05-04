package org.goldstandard.assurance.api.service.activity;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivities;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdditionalActivityCreator {

  private final ActivityFinder activityFinder;
  private final RequestActivityCreator requestActivityCreator;

  public RequestActivities includeAdditionalActivitiesIfRequired(
      Request originalRequest, Request updatedRequest, RequestActivity next) {
    return originalRequest
        .getCurrentActivity()
        .map(current -> includeAdditionalActivitiesIfRequired(updatedRequest, next, current))
        .orElse(new RequestActivities(next));
  }

  private RequestActivities includeAdditionalActivitiesIfRequired(
      Request request, RequestActivity next, RequestActivity current) {
    return RequestActivities.builder()
        .next(next)
        .others(buildAdditionalActivities(request, next, current))
        .build();
  }

  private Collection<RequestActivity> buildAdditionalActivities(
      Request request, RequestActivity next, RequestActivity current) {
    var startDate = next.getStartDate().minus(Duration.ofMillis(1));
    if (shouldIncludeCompletedRoundOneCompletenessCheck(current)) {
      return List.of(buildCompletedRoundOneCompletenessCheck(request, startDate));
    }
    if (shouldSkipReview(current, next)) {
      return List.of(buildCompletedRoundOneReview(request, current, startDate));
    }
    return Collections.emptyList();
  }

  private boolean shouldIncludeCompletedRoundOneCompletenessCheck(RequestActivity current) {
    return current.isAssignmentOfCompletenessCheck() && !current.isComplete();
  }

  private RequestActivity buildCompletedRoundOneCompletenessCheck(
      Request request, ZonedDateTime date) {
    var activity = activityFinder.findRoundOneCompletenessCheck();
    return requestActivityCreator.buildRequestActivity(activity, request).withStartAndEndDate(date);
  }

  private boolean shouldSkipReview(RequestActivity current, RequestActivity next) {
    return current.isAssignmentOfProjectReview() && next.isHarmonisationOfFeedback();
  }

  private RequestActivity buildCompletedRoundOneReview(
      Request request, RequestActivity current, ZonedDateTime date) {
    var activity =
        activityFinder
            .findNextActivity(current.getActivity())
            .orElseThrow(() -> new NotFoundException("Round One Review Activity not found."));
    return requestActivityCreator.buildSkippedRequestActivity(activity, request, date);
  }
}
