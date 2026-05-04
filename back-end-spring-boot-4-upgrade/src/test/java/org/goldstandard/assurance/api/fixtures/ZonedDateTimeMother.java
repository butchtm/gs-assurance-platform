package org.goldstandard.assurance.api.fixtures;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZonedDateTimeMother {

  private static final LocalTime NO_TIME = LocalTime.of(0, 0);

  public static ZonedDateTime now() {
    return ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
  }

  public static ZonedDateTime fixed() {
    return ZonedDateTime.of(2024, 11, 16, 10, 15, 5, 1, ZoneOffset.UTC);
  }

  public static ZonedDateTime monday() {
    return withDateOnly(2024, Month.APRIL, 1);
  }

  public static ZonedDateTime withDateOnly(int year, Month month, int dayOfMonth) {
    return withDateOnly(LocalDate.of(year, month, dayOfMonth));
  }

  public static ZonedDateTime withDateOnly(LocalDate date) {
    return ZonedDateTime.of(date, NO_TIME, ZoneOffset.UTC);
  }
}
