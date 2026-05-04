package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother;
import org.junit.jupiter.api.Test;

class MonitoringPeriodFactoryTest {

  private final MonitoringPeriodFactory factory = new MonitoringPeriodFactory();

  @Test
  void shouldBuildProjectMonitoringPeriod() {
    var creditingPeriod =
        ProjectCreditingPeriod.builder()
            .projectId(1)
            .periodId(2)
            .monitoringPeriods(Collections.emptyList())
            .build();
    var requestId = 10;
    var draftPeriod =
        RequestMonitoringPeriod.builder()
            .startDate(ZonedDateTimeMother.withDateOnly(2026, Month.JANUARY, 1))
            .endDate(ZonedDateTimeMother.withDateOnly(2026, Month.DECEMBER, 31))
            .feeSchedule(1)
            .build();

    var newPeriod =
        factory.toProjectCreditingMonitoringPeriod(creditingPeriod, requestId, draftPeriod);

    assertThat(newPeriod.getId()).isOne();
    assertThat(newPeriod.getProjectId()).isEqualTo(creditingPeriod.getProjectId());
    assertThat(newPeriod.getPeriodId()).isEqualTo(creditingPeriod.getPeriodId());
    assertThat(newPeriod.getStartDate()).isEqualTo(draftPeriod.getStartDate());
    assertThat(newPeriod.getEndDate()).isEqualTo(draftPeriod.getEndDate());
    assertThat(newPeriod.getOriginRequestId()).isEqualTo(requestId);
    assertThat(newPeriod.getFeeSchedule()).isEqualTo(draftPeriod.getFeeSchedule());
  }

  @Test
  void shouldBuildProjectNewMonitoringPeriod() {
    var creditingPeriod =
        ProjectCreditingPeriod.builder()
            .projectId(1)
            .periodId(2)
            .monitoringPeriods(List.of(ProjectCreditingMonitoringPeriod.builder().id(2).build()))
            .build();
    var requestId = 10;
    var draftPeriod = RequestMonitoringPeriod.builder().build();

    var newPeriod =
        factory.toProjectCreditingMonitoringPeriod(creditingPeriod, requestId, draftPeriod);

    assertThat(newPeriod.getId()).isEqualTo(3);
  }
}
