package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.goldstandard.assurance.api.service.WeekdayCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathwayGroupDeadlineCalculatorTest {

  @InjectMocks PathwayGroupDeadlineCalculator calculator;

  @Mock Request request;
  @Mock RequestActivityFinder requestActivityFinder;
  @Mock Clock clock;
  @Mock WeekdayCalculator weekdayCalculator;

  @Test
  void shouldReturnEmptyWhenRequestHasNoActivity() {
    when(request.getCurrentActivity()).thenReturn(Optional.empty());

    var deadline = calculator.calculateCurrentPathwayGroupEndDateForRequest(request);

    assertThat(deadline).isEmpty();
  }

  @Test
  void shouldCalculatePathwayGroupDeadline(@Mock RequestActivity requestActivity) {
    when(request.getCurrentActivity()).thenReturn(Optional.of(requestActivity));

    var now = givenTimeNow();
    var startDate = ZonedDateTimeMother.fixed();
    when(requestActivityFinder.findPathwayGroupStartDateForRequest(requestActivity, now))
        .thenReturn(startDate);

    when(requestActivity.getPathwayGroupTotalDays()).thenReturn(3);
    when(weekdayCalculator.addWeekdays(startDate, 3)).thenReturn(startDate.plusDays(3));

    var deadline = calculator.calculateCurrentPathwayGroupEndDateForRequest(request);

    var expectedDeadline = startDate.plusDays(requestActivity.getPathwayGroupTotalDays());
    assertThat(deadline).contains(expectedDeadline);
  }

  private ZonedDateTime givenTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}
