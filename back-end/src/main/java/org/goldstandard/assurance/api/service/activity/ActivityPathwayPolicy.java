package org.goldstandard.assurance.api.service.activity;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.RequestActivities;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.activity.PathwayIndexCalculator.PathwayIndexes;
import org.goldstandard.assurance.api.service.activity.RequiredActivityCalculator.RequiredActivity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ActivityPathwayPolicy {

  private final RequestActivityCreator requestActivityCreator;
  private final PathwayIndexCalculator pathwayIndexCalculator;
  private final RequiredActivityCalculator requiredActivityCalculator;
  private final ActivityFinder activityFinder;
  private final AdditionalActivityCreator additionalActivityCreator;

  public RequestActivities createNextActivitiesIfRequired(
      Request originalRequest, Request updatedRequest) {
    return raiseActivityFromPathway(updatedRequest, originalRequest.getCurrentRequestStatus())
        .map(activity -> requestActivityCreator.buildRequestActivity(activity, updatedRequest))
        .map(
            nextActivity ->
                additionalActivityCreator.includeAdditionalActivitiesIfRequired(
                    originalRequest, updatedRequest, nextActivity))
        .orElse(new RequestActivities());
  }

  private Optional<Activity> raiseActivityFromPathway(
      Request updatedRequest, RequestStatus originalStatus) {
    return pathwayIndexCalculator
        .calculatePathwayIndexes(updatedRequest, originalStatus)
        .flatMap(indexes -> findRequiredActivity(updatedRequest, originalStatus, indexes))
        .filter(newActivity -> shouldPersistNewActivity(updatedRequest, newActivity));
  }

  private Optional<Activity> findRequiredActivity(
      Request request, RequestStatus originalStatus, PathwayIndexes indexes) {
    int pathwayIndex = indexes.getPathwayIndex();
    RequiredActivity requiredActivity =
        requiredActivityCalculator.calculateRequiredActivity(request, originalStatus);
    return switch (requiredActivity) {
      case FIRST_FROM_PATHWAY -> activityFinder.findFirstActivityFromPathway(pathwayIndex);
      case FIRST_FROM_PATHWAY_GROUP ->
          activityFinder.findFirstActivityFromPathwayGroup(
              pathwayIndex, indexes.getRoundTwoPathwayGroupIndex());
      case NEXT_ACTIVITY ->
          request
              .getCurrentActivity()
              .map(RequestActivity::getActivity)
              .flatMap(activityFinder::findNextActivity);
      case SKIP_NEXT_ACTIVITY ->
          request
              .getCurrentActivity()
              .map(RequestActivity::getActivity)
              .flatMap(activityFinder::findActivityAfterNext);
      default -> Optional.empty();
    };
  }

  private boolean shouldPersistNewActivity(Request request, Activity newActivity) {
    return request
        .getCurrentActivity()
        .map(currentActivity -> shouldPersistNewActivity(currentActivity, newActivity))
        .orElse(true);
  }

  private boolean shouldPersistNewActivity(RequestActivity currentActivity, Activity newActivity) {
    if (!currentActivity.isComplete()) {
      log.warn(
          "current activity {} is not complete so cannot persist new activity {}",
          currentActivity.getActivityId(),
          newActivity.getId());
      return false;
    }
    return true;
  }
}
