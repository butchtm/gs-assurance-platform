package org.goldstandard.assurance.api.entity.activity;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;

@Builder
@RequiredArgsConstructor
@Getter
public class PathwayGroupTimeline {

  private final Request request;
  private final Collection<Activity> pathwayActivities;
  private final Collection<RequestActivity> requestActivities;

  public PathwayGroupTimeline(Request request) {
    this(request, Collections.emptyList(), Collections.emptyList());
  }

  public Optional<RequestActivity> findLatestRequestActivityFor(Activity pathwayActivity) {
    return requestActivities.stream()
        .sorted(Comparator.comparing(RequestActivity::getStartDate, Comparator.reverseOrder()))
        .filter(requestActivity -> requestActivity.hasActivityId(pathwayActivity.getId()))
        .findFirst();
  }

  public boolean isCurrentActivity(RequestActivity otherActivity) {
    return request
        .getCurrentActivity()
        .filter(currentActivity -> !currentActivity.isComplete())
        .map(currentActivity -> currentActivity.hasActivityId(otherActivity.getActivityId()))
        .orElse(false);
  }

  public boolean isForCompletenessCheckPathway() {
    return pathwayActivities.stream().anyMatch(Activity::isOnCompletenessCheckPathway);
  }
}
