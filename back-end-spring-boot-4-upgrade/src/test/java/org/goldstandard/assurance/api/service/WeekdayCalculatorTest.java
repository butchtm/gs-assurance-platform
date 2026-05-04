package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import org.goldstandard.assurance.api.exception.InvalidNumberOfWeekdaysException;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeekdayCalculatorTest {

  private final WeekdayCalculator calculator = new WeekdayCalculator();

  @Test
  void shouldNotSkipWeekdays() {
    ZonedDateTime monday = ZonedDateTimeMother.monday();
    int workingDays = 4;

    var result = calculator.addWeekdays(monday, workingDays);

    assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
    assertThat(result).isEqualTo(monday.plusDays(workingDays));
  }

  @Test
  void shouldSkipWeekendDaysAcrossOneWeek() {
    ZonedDateTime monday = ZonedDateTimeMother.monday();
    int workingDays = 5;

    var result = calculator.addWeekdays(monday, workingDays);

    assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(result).isEqualTo(monday.plusDays(workingDays + 2));
  }

  @Test
  void shouldSkipWeekendDaysAcrossMultipleWeeks() {
    ZonedDateTime monday = ZonedDateTimeMother.monday();
    int workingDays = 10;

    var result = calculator.addWeekdays(monday, workingDays);

    assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(result).isEqualTo(monday.plusDays(workingDays + 4));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void shouldNotAcceptNegativeOrZeroWorkingDays(int workingDays) {
    ZonedDateTime now = ZonedDateTimeMother.now();

    Throwable error = catchThrowable(() -> calculator.addWeekdays(now, workingDays));

    assertThat(error).isInstanceOf(InvalidNumberOfWeekdaysException.class);
  }
}
