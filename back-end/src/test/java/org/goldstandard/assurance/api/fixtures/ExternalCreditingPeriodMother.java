package org.goldstandard.assurance.api.fixtures;

import java.time.Duration;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriod;
import org.goldstandard.assurance.api.service.external.ExternalCreditingPeriodMethodology;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalCreditingPeriodMother {

  public static Collection<ExternalCreditingPeriod> buildCreditingPeriods() {
    var duration = Duration.ofDays(730);
    var startDate = ZonedDateTimeMother.withDateOnly(2024, Month.JANUARY, 1);
    return List.of(
        buildCreditingPeriod(1, startDate, duration),
        buildCreditingPeriod(2, startDate.plus(duration).plusDays(1), duration));
  }

  private static ExternalCreditingPeriod buildCreditingPeriod(
      int id, ZonedDateTime startDate, Duration duration) {
    return ExternalCreditingPeriod.builder()
        .id(id)
        .startDate(startDate)
        .endDate(startDate.plus(duration))
        .methodologies(buildMethodologies())
        .build();
  }

  private static Collection<ExternalCreditingPeriodMethodology> buildMethodologies() {
    return List.of(buildMethodology(1), buildMethodology(2));
  }

  private static ExternalCreditingPeriodMethodology buildMethodology(int id) {
    return ExternalCreditingPeriodMethodology.builder()
        .id(id)
        .version(id + 0.1)
        .description(String.format("AMC%d - methodology %d", id, id))
        .build();
  }
}
