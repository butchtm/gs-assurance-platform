package org.goldstandard.assurance.api.service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.InvalidNumberOfWeekdaysException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeekdayCalculator {

  private final Collection<DayOfWeek> weekendDays;

  public WeekdayCalculator() {
    this(buildWeekendDays());
  }

  public ZonedDateTime addWeekdays(ZonedDateTime from, int weekdays) {
    if (weekdays < 1) {
      throw new InvalidNumberOfWeekdaysException(weekdays);
    }
    ZonedDateTime result = from;
    int daysAdded = 0;
    while (daysAdded < weekdays) {
      result = result.plusDays(1);
      if (isWeekday(result)) {
        daysAdded++;
      }
    }
    return result;
  }

  private boolean isWeekday(ZonedDateTime dateTime) {
    return isWeekday(dateTime.getDayOfWeek());
  }

  private boolean isWeekday(DayOfWeek dayOfWeek) {
    return !weekendDays.contains(dayOfWeek);
  }

  private static Collection<DayOfWeek> buildWeekendDays() {
    return List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
  }
}
