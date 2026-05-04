package org.goldstandard.assurance.api.service.activity;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.entity.activity.PathwayGroup;
import org.goldstandard.assurance.api.repository.activity.ActivityRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ActivityFinder {

  private final ActivityRepository activityRepository;
  private final PathwayGroupFinder pathwayGroupFinder;

  public Optional<Activity> findFirstActivityFromPathway(int pathwayIndex) {
    return pathwayGroupFinder.findFirstPathwayGroup(pathwayIndex).flatMap(this::findFirstActivity);
  }

  public Optional<Activity> findFirstActivityFromPathwayGroup(
      int pathwayIndex, int pathwayGroupIndex) {
    return pathwayGroupFinder
        .findByPathwayAndIndex(pathwayIndex, pathwayGroupIndex)
        .flatMap(this::findFirstActivity);
  }

  public Optional<Activity> findNextActivity(Activity activity) {
    return findFutureActivity(activity, 1);
  }

  public Optional<Activity> findActivityAfterNext(Activity activity) {
    return findFutureActivity(activity, 2);
  }

  private Optional<Activity> findFutureActivity(Activity activity, int futureActivityIncrement) {
    return activityRepository.findByPathwayGroupAndIndex(
        activity.getPathwayGroup(), activity.getIndex() + futureActivityIncrement);
  }

  public Activity findRoundOneCompletenessCheck() {
    return activityRepository.findById(Activity.ROUND_1_COMPLETENESS_CHECK_ID).orElseThrow();
  }

  public int getElapsedPlannedDaysAtEndOfActivity(Activity activity) {
    return activityRepository.findByPathwayGroupOrderByIndexAsc(activity.getPathwayGroup()).stream()
        .limit(activity.getIndex() + 1L)
        .mapToInt(Activity::getTotalDays)
        .sum();
  }

  private Optional<Activity> findFirstActivity(PathwayGroup pathwayGroup) {
    return activityRepository.findFirstByPathwayGroupOrderByIndexAsc(pathwayGroup);
  }

  public Collection<Activity> findActivities(PathwayGroup pathwayGroup) {
    return activityRepository.findByPathwayGroupOrderByIndexAsc(pathwayGroup);
  }
}
