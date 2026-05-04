package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PathPrefixHandlerTest {

  private final PathPrefixHandler handler = new PathPrefixHandler();

  @ParameterizedTest
  @CsvSource({
    "s3://bucket/folder/object.txt,folder/object.txt",
    "/bucket/folder/object.txt,bucket/folder/object.txt",
    "bucket/folder/object.txt,bucket/folder/object.txt"
  })
  void shouldConvertInputToPrefix(String input, String expectedPrefix) {
    var prefix = handler.toPrefix(input);

    assertThat(prefix).isEqualTo(expectedPrefix);
  }
}
