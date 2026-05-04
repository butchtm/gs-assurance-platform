package org.goldstandard.assurance.api.service.activity;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityDeadlineCalculator {

  private final WeekdayCalculator weekdayCalculator;
  private final RequestActivityFinder requestActivityFinder;
  private final ActivityFinder activityFinder;

  public ZonedDateTime calculateDeadline(
      ZonedDateTime startDate, Activity activity, Integer requestId) {
    if (activity.isInternal()) {
      return calculateInternalDeadline(startDate, activity, requestId);
    }
    return weekdayCalculator.addWeekdays(startDate, activity.getTotalDays());
  }

  private ZonedDateTime calculateInternalDeadline(
      ZonedDateTime startDate, Activity activity, Integer requestId) {
    var pathwayGroupStartDate =
        requestActivityFinder.findPathwayGroupStartDateForRequest(requestId, activity, startDate);
    var elapsedPlannedDuration = activityFinder.getElapsedPlannedDaysAtEndOfActivity(activity);
    return weekdayCalculator.addWeekdays(pathwayGroupStartDate, elapsedPlannedDuration);
  }
}
