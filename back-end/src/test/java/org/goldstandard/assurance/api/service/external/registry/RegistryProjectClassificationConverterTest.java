package org.goldstandard.assurance.api.service.external.registry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RegistryProjectClassificationConverterTest {

  private final RegistryProjectClassificationConverter converter =
      new RegistryProjectClassificationConverter();

  @ParameterizedTest
  @MethodSource("assuranceClassificationCodeAndExpectedRegistryClassificationCode")
  void shouldConvertAssuranceClassificationCodesToRegistryClassificationCodes(
      String assuranceCountryCode, String expectedRegistryCountryCode) {
    var registryCountryCode = converter.toRegistryClassification(assuranceCountryCode);

    assertThat(registryCountryCode).isEqualTo(expectedRegistryCountryCode);
  }

  private static Stream<Arguments>
      assuranceClassificationCodeAndExpectedRegistryClassificationCode() {
    return Stream.of(
        Arguments.of("NONE", "STANDALONE"),
        Arguments.of("STANDALONE_PROJECT", "STANDALONE"),
        Arguments.of("REAL_CASE_VPA_CROSS_LINKAGE", "VPA"),
        Arguments.of("REGULAR_VPA_CROSS_LINKAGE", "VPA"));
  }
}
