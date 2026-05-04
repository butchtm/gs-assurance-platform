package org.goldstandard.assurance.api.entity.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DocumentTypeTest {

  @Test
  void shouldReturnTrueIfTypeIsOther() {
    var type = new DocumentType(999999);

    var other = type.isOther();

    assertThat(other).isTrue();
  }

  @Test
  void shouldReturnFalseIfTypeIsNotOther() {
    var type = new DocumentType(0);

    var other = type.isOther();

    assertThat(other).isFalse();
  }
}
