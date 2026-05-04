package org.goldstandard.assurance.api.entity.creditingperiod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditingPeriodUpdateTest {

  @Mock ProjectCreditingPeriod period;

  @Mock ProjectCreditingPeriodMethodology methodology;

  @Test
  void shouldBeDeletionIfOnlyHasOriginalPeriod() {
    var update = CreditingPeriodUpdate.builder().original(period).updated(null).build();

    assertThat(update.isDeletion()).isTrue();
    assertThat(update.isUpdate()).isFalse();
    assertThat(update.isAddition()).isFalse();
  }

  @Test
  void shouldBeAdditionIfOnlyHasUpdatedPeriod() {
    var update = CreditingPeriodUpdate.builder().original(null).updated(period).build();

    assertThat(update.isDeletion()).isFalse();
    assertThat(update.isUpdate()).isFalse();
    assertThat(update.isAddition()).isTrue();
  }

  @Test
  void shouldBeUpdateIfHasBothOriginalAndUpdatedPeriods() {
    var update = CreditingPeriodUpdate.builder().original(period).updated(period).build();

    assertThat(update.isDeletion()).isFalse();
    assertThat(update.isUpdate()).isTrue();
    assertThat(update.isAddition()).isFalse();
  }

  @Test
  void shouldReturnProjectIdFromOriginalPeriodIfPresent() {
    int expectedProjectId = 10;
    when(period.getProjectId()).thenReturn(expectedProjectId);
    var update = CreditingPeriodUpdate.builder().original(period).updated(null).build();

    int projectId = update.getProjectId();

    assertThat(projectId).isEqualTo(expectedProjectId);
  }

  @Test
  void shouldReturnProjectIdFromUpdatedPeriodIfOriginalPeriodNotPresent() {
    int expectedProjectId = 20;
    when(period.getProjectId()).thenReturn(expectedProjectId);
    var update = CreditingPeriodUpdate.builder().original(null).updated(period).build();

    int projectId = update.getProjectId();

    assertThat(projectId).isEqualTo(expectedProjectId);
  }

  @Test
  void shouldReturnMethodologiesFromOriginalPeriodIfPresent() {
    var expectedMethodologies = List.of(methodology);
    when(period.getMethodologies()).thenReturn(expectedMethodologies);
    var update = CreditingPeriodUpdate.builder().original(period).updated(null).build();

    var methodologies = update.getOriginalMethodologies();

    assertThat(methodologies).isEqualTo(expectedMethodologies);
  }

  @Test
  void shouldReturnEmptyMethodologiesIfOriginalPeriodNotPresent() {
    var update = CreditingPeriodUpdate.builder().original(null).updated(period).build();

    var methodologies = update.getOriginalMethodologies();

    assertThat(methodologies).isEmpty();
  }

  @Test
  void shouldReturnMethodologiesFromUpdatedPeriodIfPresent() {
    var expectedMethodologies = List.of(methodology);
    when(period.getMethodologies()).thenReturn(expectedMethodologies);
    var update = CreditingPeriodUpdate.builder().original(null).updated(period).build();

    var methodologies = update.getUpdatedMethodologies();

    assertThat(methodologies).isEqualTo(expectedMethodologies);
  }

  @Test
  void shouldReturnEmptyMethodologiesIfUpdatedPeriodNotPresent() {
    var update = CreditingPeriodUpdate.builder().original(period).updated(null).build();

    var methodologies = update.getUpdatedMethodologies();

    assertThat(methodologies).isEmpty();
  }
}
