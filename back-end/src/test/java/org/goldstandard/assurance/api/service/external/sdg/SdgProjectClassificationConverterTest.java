package org.goldstandard.assurance.api.service.external.sdg;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SdgProjectClassificationConverterTest {

  private final SdgProjectClassificationConverter converter =
      new SdgProjectClassificationConverter();

  @ParameterizedTest
  @MethodSource("assuranceClassificationCodeAndExpectedSdgClassificationCode")
  void shouldConvertAssuranceClassificationCodesToSdgClassificationCodes(
      String assuranceCountryCode, String expectedRegistryCountryCode) {
    var registryCountryCode = converter.toSdgClassification(assuranceCountryCode);

    assertThat(registryCountryCode).isEqualTo(expectedRegistryCountryCode);
  }

  private static Stream<Arguments> assuranceClassificationCodeAndExpectedSdgClassificationCode() {
    return Stream.of(Arguments.of("STANDALONE_PROJECT", "NONE"), Arguments.of("NONE", "NONE"));
  }
}
