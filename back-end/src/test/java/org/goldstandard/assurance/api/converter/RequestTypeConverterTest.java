package org.goldstandard.assurance.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestTypeConverterTest {
  private final RequestTypeConverter converter = new RequestTypeConverter();

  static Stream<Arguments> preservesIdTestCases() {
    return Stream.of(
        Arguments.of(1, "valid ID"), Arguments.of(-1, "invalid ID"), Arguments.of(null, "null ID"));
  }

  @ParameterizedTest(name = "{index}: {1} ({0}) → expected={0}")
  @MethodSource("preservesIdTestCases")
  void preservesId(Integer id, String description) {
    var requestType = RequestType.builder().id(id).build();
    assertEquals(id, converter.toDTO(requestType).getId());
  }

  static Stream<Arguments> preservesCodeTestCases() {
    return Stream.of(
        Arguments.of("hello", "valid code"),
        Arguments.of("", "empty code"),
        Arguments.of(null, "null code"));
  }

  @ParameterizedTest(name = "{index}: {1} ({0}) → expected={0}")
  @MethodSource("preservesCodeTestCases")
  void preservesCode(String code, String description) {
    var requestType = RequestType.builder().code(code).build();
    assertEquals(code, converter.toDTO(requestType).getCode());
  }

  static Stream<Arguments> preservesDescriptionTestCases() {
    return Stream.of(
        Arguments.of("hello", "valid description"),
        Arguments.of("", "empty description"),
        Arguments.of(null, "null description"));
  }

  @ParameterizedTest(name = "{index}: {1} ({0}) → expected={0}")
  @MethodSource("preservesDescriptionTestCases")
  void preservesDescription(String description, String testDescription) {
    var requestType = RequestType.builder().description(description).build();
    assertEquals(description, converter.toDTO(requestType).getDescription());
  }

  static Stream<Arguments> preservesIsActiveTestCases() {
    return Stream.of(Arguments.of(true, "true isActive"), Arguments.of(false, "false isActive"));
  }

  @ParameterizedTest(name = "{index}: {1} ({0}) → expected={0}")
  @MethodSource("preservesIsActiveTestCases")
  void mapsIsActive(boolean isActive, String description) {
    var requestType = RequestType.builder().isActive(isActive).build();
    assertEquals(isActive, converter.toDTO(requestType).getIsActive());
  }

  @Test
  void mapsNullIsActive() {
    var requestType = RequestType.builder().isActive(null).build();
    assertFalse(converter.toDTO(requestType).getIsActive());
  }

  static Stream<Arguments> calculatesSupportsPAATestCases() {
    return Stream.of(
        Arguments.of(RequestType.DESIGN_CHANGE_WITH_VVB, true, "type supports PAA"),
        Arguments.of(RequestType.GENERIC, false, "type does not support PAA"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("calculatesSupportsPAATestCases")
  void calculatesSupportsPAA(Integer id, boolean expected, String description) {
    var requestType = RequestType.builder().id(id).build();
    assertEquals(expected, converter.toDTO(requestType).isSupportsPAA());
  }
}
