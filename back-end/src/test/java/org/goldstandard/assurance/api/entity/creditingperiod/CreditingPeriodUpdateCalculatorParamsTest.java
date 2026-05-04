package org.goldstandard.assurance.api.entity.creditingperiod;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CreditingPeriodUpdateCalculatorParamsTest {

  @Test
  void shouldReturnEmptyOriginalPeriodsIfNull() {
    var params = CreditingPeriodUpdateCalculatorParams.builder().originalPeriods(null).build();

    var originalPeriods = params.getOriginalPeriods();

    assertThat(originalPeriods).isEmpty();
  }

  @Test
  void shouldReturnEmptyUpdatedPeriodsIfNull() {
    var params = CreditingPeriodUpdateCalculatorParams.builder().updatedPeriods(null).build();

    var updatedPeriods = params.getUpdatedPeriods();

    assertThat(updatedPeriods).isEmpty();
  }
}
