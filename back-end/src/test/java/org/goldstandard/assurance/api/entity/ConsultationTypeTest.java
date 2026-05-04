package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.fixtures.ConsultationTypeMother;
import org.junit.jupiter.api.Test;

class ConsultationTypeTest {

  @Test
  void shouldReturnTrueIfHasTacType() {
    ConsultationType type = ConsultationTypeMother.tacType();

    boolean tac = type.isTac();

    assertThat(tac).isTrue();
  }

  @Test
  void shouldReturnFalseIfDoesNotHaveTacType() {
    ConsultationType type = ConsultationTypeMother.publicType();

    boolean tac = type.isTac();

    assertThat(tac).isFalse();
  }
}
