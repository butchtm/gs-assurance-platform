package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditingPeriodCalculatorTest {

  private static final ZonedDateTime NOW = ZonedDateTime.now();

  @InjectMocks CreditingPeriodCalculator calculator;

  @Mock Clock clock;

  @Mock AuthenticatedUserSupplier userSupplier;

  @Mock Account account;

  @BeforeEach
  void setUp() {
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(NOW.toInstant());
    when(userSupplier.getAuthenticatedUserAccount()).thenReturn(Optional.of(account));
  }

  @Test
  void shouldReturnDeletionIfPeriodDeleted() {
    var originalPeriod = buildPeriod();

    var update = calculator.calculateUpdates(List.of(originalPeriod), Collections.emptyList());

    var deletions = update.getDeletions();
    assertThat(deletions).hasSize(1);
    assertThat(update.getAdditions()).isEmpty();
    assertThat(update.getUpdates()).isEmpty();
    var deletion = IterableUtils.get(deletions, 0);
    assertThat(deletion.getUpdatedAt()).isEqualTo(NOW);
    assertThat(deletion.getUpdatedBy()).isEqualTo(account);
    assertThat(deletion.getId()).isEqualTo(originalPeriod.getPeriodId());
    assertThat(deletion.getOriginal()).isEqualTo(originalPeriod);
    assertThat(deletion.getUpdated()).isNull();
  }

  @Test
  void shouldReturnAdditionIfPeriodAdded() {
    var updatedPeriod = buildPeriod();

    var update = calculator.calculateUpdates(Collections.emptyList(), List.of(updatedPeriod));

    var additions = update.getAdditions();
    assertThat(additions).hasSize(1);
    assertThat(update.getDeletions()).isEmpty();
    assertThat(update.getUpdates()).isEmpty();
    var addition = IterableUtils.get(additions, 0);
    assertThat(addition.getUpdatedAt()).isEqualTo(NOW);
    assertThat(addition.getUpdatedBy()).isEqualTo(account);
    assertThat(addition.getId()).isEqualTo(updatedPeriod.getPeriodId());
    assertThat(addition.getOriginal()).isNull();
    assertThat(addition.getUpdated()).isEqualTo(updatedPeriod);
  }

  @Test
  void shouldReturnUpdateIfPeriodUpdated() {
    var originalPeriod = buildPeriod();
    var updatedPeriod = buildPeriod();

    var update = calculator.calculateUpdates(List.of(originalPeriod), List.of(updatedPeriod));

    var updates = update.getUpdates();
    assertThat(updates).hasSize(1);
    assertThat(update.getAdditions()).isEmpty();
    assertThat(update.getDeletions()).isEmpty();
    var periodUpdate = IterableUtils.get(updates, 0);
    assertThat(periodUpdate.getUpdatedAt()).isEqualTo(NOW);
    assertThat(periodUpdate.getUpdatedBy()).isEqualTo(account);
    assertThat(periodUpdate.getId()).isEqualTo(originalPeriod.getPeriodId());
    assertThat(periodUpdate.getOriginal()).isEqualTo(originalPeriod);
    assertThat(periodUpdate.getUpdated()).isEqualTo(updatedPeriod);
  }

  private static ProjectCreditingPeriod buildPeriod() {
    var now = ZonedDateTime.now();
    var project = Project.builder().id(999).build();
    return ProjectCreditingPeriod.builder()
        .project(project)
        .projectId(project.getId())
        .periodId(1)
        .startDate(now.minusDays(2))
        .endDate(now.plusDays(2))
        .methodologies(Collections.emptyList())
        .build();
  }
}
