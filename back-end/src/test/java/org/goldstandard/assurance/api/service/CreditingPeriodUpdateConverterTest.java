package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.creditingperiod.CreditingPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.MonitoringPeriodUpdate;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology.ProjectCreditingPeriodMethodologyBuilder;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;
import org.goldstandard.assurance.api.enumeration.MonitoringPeriodOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditingPeriodUpdateConverterTest {

  private final CreditingPeriodUpdateConverter converter = new CreditingPeriodUpdateConverter();

  @Mock CreditingPeriodUpdate periodUpdate;

  @Mock ProjectCreditingPeriod updated;

  @Mock ProjectCreditingPeriod original;

  @Test
  void shouldReturnMonitoringPeriodAdditionsIfNewPeriodIsAdded() {
    when(periodUpdate.isAddition()).thenReturn(true);
    when(periodUpdate.getUpdated()).thenReturn(updated);
    var monitoringPeriod = buildMonitoringPeriod();
    when(updated.getMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).hasSize(1);
    var monitoringPeriodUpdate = IterableUtils.get(monitoringPeriodUpdates, 0);
    assertMonitoringPeriodsCalculatedCorrectly(
        monitoringPeriodUpdate, monitoringPeriod, MonitoringPeriodOperation.ADD);
  }

  @Test
  void shouldReturnMonitoringPeriodDeletionsIfPeriodIsDeleted() {
    when(periodUpdate.isDeletion()).thenReturn(true);
    when(periodUpdate.getOriginal()).thenReturn(original);
    var monitoringPeriod = buildMonitoringPeriod();
    when(original.getMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).hasSize(1);
    var monitoringPeriodUpdate = IterableUtils.get(monitoringPeriodUpdates, 0);
    assertMonitoringPeriodsCalculatedCorrectly(
        monitoringPeriodUpdate, monitoringPeriod, MonitoringPeriodOperation.DELETE);
  }

  @Test
  void shouldReturnMonitoringPeriodAdditionsIfMonitoringPeriodAddedToPeriod() {
    var monitoringPeriod = buildMonitoringPeriod();
    when(periodUpdate.getUpdatedMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).hasSize(1);
    var monitoringPeriodUpdate = IterableUtils.get(monitoringPeriodUpdates, 0);
    assertMonitoringPeriodsCalculatedCorrectly(
        monitoringPeriodUpdate, monitoringPeriod, MonitoringPeriodOperation.ADD);
  }

  @Test
  void shouldReturnMonitoringPeriodDeletionsIfMonitoringPeriodRemovedFromPeriod() {
    when(periodUpdate.getUpdatedMonitoringPeriods()).thenReturn(Collections.emptyList());
    var monitoringPeriod = buildMonitoringPeriod();
    when(periodUpdate.getOriginalMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).hasSize(1);
    var monitoringPeriodUpdate = IterableUtils.get(monitoringPeriodUpdates, 0);
    assertMonitoringPeriodsCalculatedCorrectly(
        monitoringPeriodUpdate, monitoringPeriod, MonitoringPeriodOperation.DELETE);
  }

  @Test
  void shouldReturnMonitoringPeriodUpdateIfDatesChanged() {
    var now = ZonedDateTime.now();
    var originalMonitoringPeriod =
        buildMonitoringPeriod().toBuilder().startDate(now).endDate(now.plusDays(10)).build();
    when(periodUpdate.getOriginalMonitoringPeriods()).thenReturn(List.of(originalMonitoringPeriod));
    var updatedMonitoringPeriod =
        buildMonitoringPeriod().toBuilder()
            .startDate(now.plusDays(1))
            .endDate(now.plusDays(11))
            .build();
    when(periodUpdate.getUpdatedMonitoringPeriods()).thenReturn(List.of(updatedMonitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).hasSize(1);
    var monitoringPeriodUpdate = IterableUtils.get(monitoringPeriodUpdates, 0);
    assertMonitoringPeriodsCalculatedCorrectly(
        monitoringPeriodUpdate, updatedMonitoringPeriod, MonitoringPeriodOperation.UPDATE);
    assertThat(monitoringPeriodUpdate.getUpdatedPeriod().getStartDate())
        .isEqualTo(updatedMonitoringPeriod.getStartDate());
    assertThat(monitoringPeriodUpdate.getUpdatedPeriod().getEndDate())
        .isEqualTo(updatedMonitoringPeriod.getEndDate());
  }

  @Test
  void shouldReturnEmptyListIfNoMonitoringPeriodsChanged() {
    var monitoringPeriod = buildMonitoringPeriod();
    when(periodUpdate.getOriginalMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));
    when(periodUpdate.getUpdatedMonitoringPeriods()).thenReturn(List.of(monitoringPeriod));

    var monitoringPeriodUpdates = converter.toMonitoringPeriodUpdates(periodUpdate);

    assertThat(monitoringPeriodUpdates).isEmpty();
  }

  @Test
  void shouldReturnMethodologyAdditionsIfNewPeriodIsAdded() {
    when(periodUpdate.isAddition()).thenReturn(true);
    when(periodUpdate.getUpdated()).thenReturn(updated);
    var methodology = buildMethodology();
    when(updated.getMethodologies()).thenReturn(List.of(methodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).hasSize(1);
    var methodologyUpdate = IterableUtils.get(methodologyUpdates, 0);
    assertThat(methodologyUpdate.getProjectId()).isEqualTo(methodology.getProjectId());
    assertThat(methodologyUpdate.getPeriodId()).isEqualTo(methodology.getPeriodId());
    assertThat(methodologyUpdate.getMethodologyId()).isEqualTo(methodology.getMethodologyId());
    assertThat(methodologyUpdate.getOriginalVersion()).isNull();
    assertThat(methodologyUpdate.getUpdatedVersion()).isEqualTo(methodology.getVersion());
    assertThat(methodologyUpdate.getOperation()).isEqualTo(MethodologyOperation.ADD);
  }

  @Test
  void shouldReturnMethodologyDeletionsIfPeriodIsDeleted() {
    when(periodUpdate.isDeletion()).thenReturn(true);
    when(periodUpdate.getOriginal()).thenReturn(original);
    var methodology = buildMethodology();
    when(original.getMethodologies()).thenReturn(List.of(methodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).hasSize(1);
    var methodologyUpdate = IterableUtils.get(methodologyUpdates, 0);
    assertThat(methodologyUpdate.getProjectId()).isEqualTo(methodology.getProjectId());
    assertThat(methodologyUpdate.getPeriodId()).isEqualTo(methodology.getPeriodId());
    assertThat(methodologyUpdate.getMethodologyId()).isEqualTo(methodology.getMethodologyId());
    assertThat(methodologyUpdate.getOriginalVersion()).isEqualTo(methodology.getVersion());
    assertThat(methodologyUpdate.getUpdatedVersion()).isNull();
    assertThat(methodologyUpdate.getOperation()).isEqualTo(MethodologyOperation.DELETE);
  }

  @Test
  void shouldReturnMethodologyAdditionsIfMethodologyAddedToPeriod() {
    when(periodUpdate.getOriginalMethodologies()).thenReturn(Collections.emptyList());
    var methodology = buildMethodology();
    when(periodUpdate.getUpdatedMethodologies()).thenReturn(List.of(methodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).hasSize(1);
    var methodologyUpdate = IterableUtils.get(methodologyUpdates, 0);
    assertThat(methodologyUpdate.getProjectId()).isEqualTo(methodology.getProjectId());
    assertThat(methodologyUpdate.getPeriodId()).isEqualTo(methodology.getPeriodId());
    assertThat(methodologyUpdate.getMethodologyId()).isEqualTo(methodology.getMethodologyId());
    assertThat(methodologyUpdate.getOriginalVersion()).isNull();
    assertThat(methodologyUpdate.getUpdatedVersion()).isEqualTo(methodology.getVersion());
    assertThat(methodologyUpdate.getOperation()).isEqualTo(MethodologyOperation.ADD);
  }

  @Test
  void shouldReturnMethodologyDeletionsIfMethodologyDeletedFromPeriod() {
    when(periodUpdate.getUpdatedMethodologies()).thenReturn(Collections.emptyList());
    var methodology = buildMethodology();
    when(periodUpdate.getOriginalMethodologies()).thenReturn(List.of(methodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).hasSize(1);
    var methodologyUpdate = IterableUtils.get(methodologyUpdates, 0);
    assertThat(methodologyUpdate.getProjectId()).isEqualTo(methodology.getProjectId());
    assertThat(methodologyUpdate.getPeriodId()).isEqualTo(methodology.getPeriodId());
    assertThat(methodologyUpdate.getMethodologyId()).isEqualTo(methodology.getMethodologyId());
    assertThat(methodologyUpdate.getOriginalVersion()).isEqualTo(methodology.getVersion());
    assertThat(methodologyUpdate.getUpdatedVersion()).isNull();
    assertThat(methodologyUpdate.getOperation()).isEqualTo(MethodologyOperation.DELETE);
  }

  @Test
  void shouldReturnMethodologyUpdateIfMethodologyVersionChanged() {
    var originalMethodology = methodologyBuilder().version(1.1).build();
    when(periodUpdate.getOriginalMethodologies()).thenReturn(List.of(originalMethodology));
    var updatedMethodology = methodologyBuilder().version(2.2).build();
    when(periodUpdate.getUpdatedMethodologies()).thenReturn(List.of(updatedMethodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).hasSize(1);
    var methodologyUpdate = IterableUtils.get(methodologyUpdates, 0);
    assertThat(methodologyUpdate.getProjectId()).isEqualTo(originalMethodology.getProjectId());
    assertThat(methodologyUpdate.getPeriodId()).isEqualTo(originalMethodology.getPeriodId());
    assertThat(methodologyUpdate.getMethodologyId())
        .isEqualTo(originalMethodology.getMethodologyId());
    assertThat(methodologyUpdate.getOriginalVersion()).isEqualTo(originalMethodology.getVersion());
    assertThat(methodologyUpdate.getUpdatedVersion()).isEqualTo(updatedMethodology.getVersion());
    assertThat(methodologyUpdate.getOperation()).isEqualTo(MethodologyOperation.UPDATE);
  }

  @Test
  void shouldReturnEmptyListIfNoMethodologiesChanged() {
    var methodology = buildMethodology();
    when(periodUpdate.getUpdatedMethodologies()).thenReturn(List.of(methodology));
    when(periodUpdate.getOriginalMethodologies()).thenReturn(List.of(methodology));

    var methodologyUpdates = converter.toMethodologyUpdates(periodUpdate);

    assertThat(methodologyUpdates).isEmpty();
  }

  private ProjectCreditingPeriodMethodology buildMethodology() {
    return methodologyBuilder().build();
  }

  private ProjectCreditingPeriodMethodologyBuilder methodologyBuilder() {
    int methodologyId = 10;
    return ProjectCreditingPeriodMethodology.builder()
        .projectId(999)
        .periodId(1)
        .methodologyId(methodologyId)
        .methodology(new Methodology(methodologyId))
        .version(2.5);
  }

  private ProjectCreditingMonitoringPeriod buildMonitoringPeriod() {
    return ProjectCreditingMonitoringPeriod.builder().id(1).projectId(999).periodId(1).build();
  }

  private static void assertMonitoringPeriodsCalculatedCorrectly(
      MonitoringPeriodUpdate monitoringPeriodUpdate,
      ProjectCreditingMonitoringPeriod monitoringPeriod,
      MonitoringPeriodOperation operation) {
    assertThat(monitoringPeriodUpdate.getProjectId()).isEqualTo(monitoringPeriod.getProjectId());
    assertThat(monitoringPeriodUpdate.getPeriodId()).isEqualTo(monitoringPeriod.getPeriodId());
    assertThat(monitoringPeriodUpdate.getOperation()).isEqualTo(operation);
  }
}
