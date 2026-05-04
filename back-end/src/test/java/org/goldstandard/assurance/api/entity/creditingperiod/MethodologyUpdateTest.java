package org.goldstandard.assurance.api.entity.creditingperiod;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.enumeration.MethodologyOperation;
import org.junit.jupiter.api.Test;

class MethodologyUpdateTest {

  @Test
  void shouldReturnTrueIfUpdateOperationIsDeletion() {
    var update = MethodologyUpdate.builder().operation(MethodologyOperation.DELETE).build();

    var deletion = update.isDeletion();

    assertThat(deletion).isTrue();
  }

  @Test
  void shouldReturnFalseIfUpdateOperationIsNotDeletion() {
    var update = MethodologyUpdate.builder().operation(MethodologyOperation.UPDATE).build();

    var deletion = update.isDeletion();

    assertThat(deletion).isFalse();
  }
}
