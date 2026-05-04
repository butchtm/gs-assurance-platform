package org.goldstandard.assurance.api.service.activity;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PathwayGroupDeadlineCalculator {
  private final RequestActivityFinder requestActivityFinder;
  private final WeekdayCalculator weekdayCalculator;
  private final Clock clock;

  public Optional<ZonedDateTime> calculateCurrentPathwayGroupEndDateForRequest(Request request) {
    return request.getCurrentActivity().map(this::calculateCurrentPathwayGroupEndDate);
  }

  public ZonedDateTime calculateCurrentPathwayGroupEndDate(RequestActivity requestActivity) {
    var startDate =
        requestActivityFinder.findPathwayGroupStartDateForRequest(
            requestActivity, ZonedDateTime.now(clock));
    return weekdayCalculator.addWeekdays(startDate, requestActivity.getPathwayGroupTotalDays());
  }
}
