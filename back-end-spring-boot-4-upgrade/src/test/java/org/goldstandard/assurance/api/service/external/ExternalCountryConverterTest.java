package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalCountryConverterTest {

  private final ExternalCountryConverter converter = new ExternalCountryConverter();

  @ParameterizedTest
  @MethodSource("assuranceCountryCodeAndExpectedRegistryCountyCode")
  void shouldConvertAssuranceCountryCodesToExternalCountryCodes(
      String assuranceCountryCode, String expectedRegistryCountryCode) {
    var registryCountryCode = converter.toExternalCountry(assuranceCountryCode);

    assertThat(registryCountryCode).isEqualTo(expectedRegistryCountryCode);
  }

  private static Stream<Arguments> assuranceCountryCodeAndExpectedRegistryCountyCode() {
    return Stream.of(
        Arguments.of("INTERNATIONAL", "XZ"),
        Arguments.of("XC", "GB"),
        Arguments.of("XE", "SC"),
        Arguments.of("PS", "XG"),
        Arguments.of("AD", "AD"),
        Arguments.of("CY", "CY"),
        Arguments.of("GM", "GM"),
        Arguments.of("ANY_OTHER_CODE", "ANY_OTHER_CODE"));
  }
}
