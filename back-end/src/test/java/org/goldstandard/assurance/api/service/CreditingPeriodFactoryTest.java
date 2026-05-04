package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreditingPeriodFactoryTest {

  public static final int PROJECT_ID = 1;
  public static final int CREDITING_PERIOD_ID = 2;
  public static final int REQUEST_ID = 101;

  @InjectMocks CreditingPeriodFactory creditingPeriodFactory;
  @Mock MonitoringPeriodFactory monitoringPeriodFactory;

  @Test
  void shouldErrorIfMonitoringPeriodDatesOutsideCreditingPeriod() {
    var now = ZonedDateTime.now();
    var monitoringPeriodEndDate = now.plusYears(1).plusDays(1);

    var creditingPeriod = buildCurrentCreditingPeriod(now).build();
    var draftMonitoringPeriod =
        RequestMonitoringPeriod.builder()
            .startDate(now)
            .endDate(monitoringPeriodEndDate)
            .feeSchedule(1)
            .build();

    Throwable error =
        catchThrowable(
            () ->
                creditingPeriodFactory.addMonitoringPeriod(
                    creditingPeriod, REQUEST_ID, draftMonitoringPeriod));

    assertThat(error)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("New Monitoring Period dates must fall within Crediting Period dates");
  }

  @Test
  void shouldErrorIfMonitoringPeriodDatesOverlap() {
    var now = ZonedDateTime.now();
    var monitoringPeriod = buildPastMonitoringPeriod();
    var creditingPeriod = buildCreditingPeriodWithMonitoringPeriod(now, monitoringPeriod);

    var newMonitoringPeriodStartDate = ZonedDateTime.now().minusDays(2);
    var newMonitoringPeriodEndDate = newMonitoringPeriodStartDate.plusYears(1);
    var draftMonitoringPeriod =
        RequestMonitoringPeriod.builder()
            .startDate(newMonitoringPeriodStartDate)
            .endDate(newMonitoringPeriodEndDate)
            .feeSchedule(1)
            .build();

    Throwable error =
        catchThrowable(
            () ->
                creditingPeriodFactory.addMonitoringPeriod(
                    creditingPeriod, REQUEST_ID, draftMonitoringPeriod));

    assertThat(error)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "New Monitoring Period dates must not overlap with existing Monitoring Periods");
  }

  @Test
  void shouldAddMonitoringPeriod() {
    var now = ZonedDateTime.now();
    var monitoringPeriod = buildPastMonitoringPeriod();
    var newMonitoringPeriodEndDate = now.plusYears(1);
    var feeSchedule = 1;
    var expectedMonitoringPeriod =
        ProjectCreditingMonitoringPeriod.builder()
            .id(2)
            .projectId(PROJECT_ID)
            .periodId(CREDITING_PERIOD_ID)
            .startDate(now)
            .endDate(newMonitoringPeriodEndDate)
            .build();
    var draftMonitoringPeriod =
        RequestMonitoringPeriod.builder()
            .startDate(now)
            .endDate(newMonitoringPeriodEndDate)
            .feeSchedule(1)
            .build();

    var creditingPeriod = buildCreditingPeriodWithMonitoringPeriod(now, monitoringPeriod);
    when(monitoringPeriodFactory.toProjectCreditingMonitoringPeriod(
            creditingPeriod, REQUEST_ID, draftMonitoringPeriod))
        .thenReturn(expectedMonitoringPeriod);

    var newCreditingPeriod =
        creditingPeriodFactory.addMonitoringPeriod(
            creditingPeriod, REQUEST_ID, draftMonitoringPeriod);

    var monitoringPeriods = newCreditingPeriod.getMonitoringPeriods();
    assertThat(monitoringPeriods).hasSize(2);
    var newMonitoringPeriod =
        monitoringPeriods.stream()
            .filter(period -> period.getId().equals(expectedMonitoringPeriod.getId()))
            .findFirst()
            .orElseThrow();
    assertThat(newMonitoringPeriod.getProjectId())
        .isEqualTo(expectedMonitoringPeriod.getProjectId());
    assertThat(newMonitoringPeriod.getPeriodId()).isEqualTo(expectedMonitoringPeriod.getPeriodId());
    assertThat(newMonitoringPeriod.getStartDate())
        .isEqualTo(expectedMonitoringPeriod.getStartDate());
    assertThat(newMonitoringPeriod.getEndDate()).isEqualTo(expectedMonitoringPeriod.getEndDate());
    assertThat(newMonitoringPeriod.getOriginRequestId())
        .isEqualTo(expectedMonitoringPeriod.getOriginRequestId());
  }

  private static ProjectCreditingPeriod.ProjectCreditingPeriodBuilder buildCurrentCreditingPeriod(
      ZonedDateTime now) {
    var creditingPeriodStartDate = now.minusYears(4);
    var creditingPeriodEndDate = now.plusYears(1);

    return ProjectCreditingPeriod.builder()
        .projectId(PROJECT_ID)
        .periodId(CREDITING_PERIOD_ID)
        .startDate(creditingPeriodStartDate)
        .endDate(creditingPeriodEndDate);
  }

  private static ProjectCreditingPeriod buildCreditingPeriodWithMonitoringPeriod(
      ZonedDateTime now, ProjectCreditingMonitoringPeriod monitoringPeriod) {
    var creditingPeriod = buildCurrentCreditingPeriod(now);

    return creditingPeriod.monitoringPeriods(List.of(monitoringPeriod)).build();
  }

  private static ProjectCreditingMonitoringPeriod buildPastMonitoringPeriod() {
    var monitoringPeriodEndDate = ZonedDateTime.now().minusDays(1);
    var monitoringPeriodStartDate = monitoringPeriodEndDate.minusYears(2);
    return ProjectCreditingMonitoringPeriod.builder()
        .id(1)
        .projectId(PROJECT_ID)
        .periodId(CREDITING_PERIOD_ID)
        .startDate(monitoringPeriodStartDate)
        .endDate(monitoringPeriodEndDate)
        .build();
  }
}
