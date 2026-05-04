package org.goldstandard.assurance.api.service.external;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExternalProjectStatusConverterTest {

  private final ExternalProjectStatusConverter converter = new ExternalProjectStatusConverter();

  @ParameterizedTest
  @MethodSource("assuranceStatusCodeAndExpectedRegistryStatusCode")
  void shouldConvertAssuranceStatusCodesToRegistryStatusCodes(
      String assuranceStatusCode, String expectedRegistryStatusCode) {
    var registryStatusCode = converter.toExternalProjectStatus(assuranceStatusCode);

    assertThat(registryStatusCode).isEqualTo(expectedRegistryStatusCode);
  }

  private static Stream<Arguments> assuranceStatusCodeAndExpectedRegistryStatusCode() {
    return Stream.of(
        Arguments.of("NEW", "NEW_PROJECT"),
        Arguments.of("DESIGN_CERTIFIED", "GOLD_STANDARD_CERTIFIED_DESIGN"),
        Arguments.of("CERTIFIED_PROJECT", "GOLD_STANDARD_CERTIFIED_PROJECT"),
        Arguments.of("DE_CERTIFIED", "DE_REGISTERED"),
        Arguments.of("ANY_OTHER_CODE", "ANY_OTHER_CODE"));
  }
}
