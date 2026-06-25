package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalProjectClassificationConverterTest {

  private static final String NONE_CODE = "NONE_CODE";

  private final ExternalProjectClassificationConverter converter =
      new ExternalProjectClassificationConverter(NONE_CODE);

  @ParameterizedTest
  @MethodSource("assuranceClassificationCodeAndExpectedExternalClassificationCode")
  void shouldConvertAssuranceClassificationCodesToExternalClassificationCodes(
      String assuranceCountryCode, String expectedRegistryCountryCode) {
    var registryCountryCode = converter.toExternalClassification(assuranceCountryCode);

    assertThat(registryCountryCode).isEqualTo(expectedRegistryCountryCode);
  }

  private static Stream<Arguments>
      assuranceClassificationCodeAndExpectedExternalClassificationCode() {
    return Stream.of(
        Arguments.of("NONE", NONE_CODE),
        Arguments.of("STANDALONE_PROJECT", NONE_CODE),
        Arguments.of("POA", "POA"),
        Arguments.of("PROGRAMME_OF_ACTIVITIES", "POA"),
        Arguments.of("REAL_OR_REGULAR_CASE_POA", "POA"),
        Arguments.of("REAL_CASE_VPA", "VPA"),
        Arguments.of("REAL_CASE_VPA_CROSS_LINKAGE", "VPA"),
        Arguments.of("REAL_CASE_VPA_LEGACY", "VPA"),
        Arguments.of("REGULAR_CASE_VPA", "VPA"),
        Arguments.of("REGULAR_VPA", "VPA"),
        Arguments.of("REGULAR_VPA_CROSS_LINKAGE", "VPA"),
        Arguments.of("VPA_CPA", "VPA"),
        Arguments.of("ANY_OTHER_CODE", "ANY_OTHER_CODE"));
  }
}
