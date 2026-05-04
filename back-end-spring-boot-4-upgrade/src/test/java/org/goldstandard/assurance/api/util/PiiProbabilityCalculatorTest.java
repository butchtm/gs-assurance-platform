package org.goldstandard.assurance.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.util.PiiProbabilityCalculator.toProbability;

import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.junit.jupiter.api.Test;

class PiiProbabilityCalculatorTest {

  @Test
  void shouldReturnHighIfScoreMeetsHighValue() {
    assertThat(toProbability(0.90f)).isEqualTo(PiiProbability.HIGH);
  }

  @Test
  void shouldReturnMediumIfScoreMeetsMediumValue() {
    assertThat(toProbability(0.70f)).isEqualTo(PiiProbability.MEDIUM);
  }

  @Test
  void shouldReturnLowIfLowerThanMediumValue() {
    assertThat(toProbability(0.40f)).isEqualTo(PiiProbability.LOW);
  }
}
