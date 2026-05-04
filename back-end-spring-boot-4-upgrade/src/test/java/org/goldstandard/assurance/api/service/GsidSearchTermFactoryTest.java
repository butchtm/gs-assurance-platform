package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GsidSearchTermFactoryTest {

  @ParameterizedTest
  @CsvSource({
    "PROJECT NAME,gsproject name",
    "project name,gsproject name",
    "GS1234,gs1234",
    "1234,gs1234",
    "gs1234,gs1234"
  })
  void shouldLowerCaseAndPrefixLowerGsIfNotPresent(String input, String expectedOutput) {
    String output = GsidSearchTermFactory.toGsidSearchTerm(input);

    assertThat(output).isEqualTo(expectedOutput);
  }

  @ParameterizedTest
  @CsvSource({"INPUT,%input%", "input,%input%"})
  void shouldLowerCaseAndWrapInPercents(String input, String expectedOutput) {
    String output = GsidSearchTermFactory.toLowerCaseWithWildcards(input);

    assertThat(output).isEqualTo(expectedOutput);
  }
}
