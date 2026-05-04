package org.goldstandard.assurance.api.entity.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReferenceDataTypeTest {

  @Test
  void shouldBuildCodeWithoutSpecialCharacters() {
    var description = "!!!';#][ 123 _-_ test !!!'#;[]";
    var expectedCode = "123_TEST";
    var result = ReferenceDataType.buildCode(description);
    assertThat(result).isEqualTo(expectedCode);
  }

  @Test
  void shouldReturnEmptyStringIfNullDescription() {
    var result = ReferenceDataType.buildCode(null);
    assertThat(result).isEmpty();
  }
}
