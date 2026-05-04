package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.entity.activity.Activity;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityDeadlineCalculatorTest {

  @InjectMocks ActivityDeadlineCalculator calculator;

  @Mock RequestActivityFinder requestActivityFinder;
  @Mock ActivityFinder activityFinder;
  @Mock WeekdayCalculator weekdayCalculator;

  @Mock Activity activity;

  @Test
  void shouldCalculateExternalDeadlineByAddingTotalActivityDaysToStartDate() {
    var requestId = 1;
    when(activity.isInternal()).thenReturn(false);
    var totalActivityDays = 5;
    when(activity.getTotalDays()).thenReturn(totalActivityDays);
    var now = ZonedDateTime.now();
    var expectedDeadline = now.plusDays(totalActivityDays);
    when(weekdayCalculator.addWeekdays(now, totalActivityDays)).thenReturn(expectedDeadline);

    var deadline = calculator.calculateDeadline(now, activity, requestId);

    assertThat(deadline).isEqualTo(expectedDeadline);
  }

  @Test
  void shouldCalculateInternalDeadlineByAddingPlannedDurationDaysToPathwayGroupStartDate() {
    var requestId = 2;
    when(activity.isInternal()).thenReturn(true);
    var now = ZonedDateTime.now();
    var expectedPlannedEndDate = givenExpectedPlannedEndDate(requestId, now);

    var deadline = calculator.calculateDeadline(now, activity, requestId);

    assertThat(deadline).isEqualTo(expectedPlannedEndDate);
  }

  private ZonedDateTime givenExpectedPlannedEndDate(int requestId, ZonedDateTime now) {
    var groupStartDate = now.minusDays(2);
    when(requestActivityFinder.findPathwayGroupStartDateForRequest(requestId, activity, now))
        .thenReturn(groupStartDate);
    var plannedDurationDays = 5;
    when(activityFinder.getElapsedPlannedDaysAtEndOfActivity(activity))
        .thenReturn(plannedDurationDays);
    var expectedDate = groupStartDate.plusDays(plannedDurationDays);
    when(weekdayCalculator.addWeekdays(groupStartDate, plannedDurationDays))
        .thenReturn(expectedDate);
    return expectedDate;
  }
}
