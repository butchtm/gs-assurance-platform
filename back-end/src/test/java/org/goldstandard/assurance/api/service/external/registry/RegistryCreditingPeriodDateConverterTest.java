package org.goldstandard.assurance.api.service.external.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother.withDateOnly;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistryCreditingPeriodDateConverterTest {

  @Mock Project project;

  @Mock ProjectCreditingPeriod period1;

  @Mock ProjectCreditingPeriod period2;

  private final RegistryProjectCreditingPeriodDateConverter converter =
      new RegistryProjectCreditingPeriodDateConverter();

  @Test
  void shouldReturnNullEarliestCreditingPeriodStartDateIfCreditingPeriodsIsNull() {
    when(project.getCreditingPeriods()).thenReturn(null);

    var startDate = converter.toEarliestCreditingPeriodStartDate(project);

    assertThat(startDate).isNull();
  }

  @Test
  void shouldReturnNullLatestCreditingPeriodEndDateIfCreditingPeriodsIsNull() {
    when(project.getCreditingPeriods()).thenReturn(null);

    var endDate = converter.toLatestCreditingPeriodEndDate(project);

    assertThat(endDate).isNull();
  }

  @Test
  void shouldConvertEarliestCreditingPeriodStartDate() {
    var expectedStartDate = withDateOnly(2024, Month.JANUARY, 1);
    when(period1.getStartDate()).thenReturn(expectedStartDate);
    when(period2.getStartDate()).thenReturn(withDateOnly(2025, Month.JANUARY, 1));
    when(project.getCreditingPeriods()).thenReturn(List.of(period1, period2));

    var startDate = converter.toEarliestCreditingPeriodStartDate(project);

    assertThat(startDate).isEqualTo(expectedStartDate);
  }

  @Test
  void shouldConvertLatestCreditingPeriodEndDate() {
    var expectedEndDate = withDateOnly(2025, Month.DECEMBER, 31);
    when(period1.getEndDate()).thenReturn(withDateOnly(2024, Month.DECEMBER, 31));
    when(period2.getEndDate()).thenReturn(expectedEndDate);
    when(project.getCreditingPeriods()).thenReturn(List.of(period1, period2));

    var endDate = converter.toLatestCreditingPeriodEndDate(project);

    assertThat(endDate).isEqualTo(expectedEndDate);
  }

  @Test
  void shouldReturnNullStartDateForNoCreditingPeriods() {
    var startDate = converter.toEarliestCreditingPeriodStartDate(project);

    assertThat(startDate).isNull();
  }

  @Test
  void shouldReturnNullEndDateForNoCreditingPeriods() {
    var startDate = converter.toLatestCreditingPeriodEndDate(project);

    assertThat(startDate).isNull();
  }
}
