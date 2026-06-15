package org.goldstandard.assurance.api.entity.lookup;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.goldstandard.assurance.api.fixtures.RequestTypeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestTypeTest {
  static Stream<Arguments> preliminaryReviewTestCases() {
    return Stream.of(
        Arguments.of(RequestType.PRELIMINARY_REVIEW_ID, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("preliminaryReviewTestCases")
  void isPreliminaryReview(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).isPreliminaryReview());
  }

  static Stream<Arguments> issuanceTestCases() {
    return Stream.of(
        Arguments.of(RequestType.ISSUANCE, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("issuanceTestCases")
  void isIssuance(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).isIssuance());
  }

  static Stream<Arguments> performanceCertificationTestCases() {
    return Stream.of(
        Arguments.of(RequestType.PERFORMANCE_CERTIFICATION_PROJECT, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("performanceCertificationTestCases")
  void isOneOfPerformanceCertification(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).isOneOfPerformanceCertification());
  }

  static Stream<Arguments> inclusionTestCases() {
    return Stream.of(
        Arguments.of(RequestType.INCLUSION_REGULAR_VPA, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("inclusionTestCases")
  void isInclusionRegularCase(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).isInclusionRegularCase());
  }

  static Stream<Arguments> combinedDesignAndPerformanceCertificationTestCases() {
    return Stream.of(
        Arguments.of(
            RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("combinedDesignAndPerformanceCertificationTestCases")
  void isOneOfCombinedDesignAndPerformanceCertification(
      Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).isOneOfCombinedDesignAndPerformanceCertification());
  }

  static Stream<Arguments> allowsMethodologiesTestCases() {
    return Stream.of(
        Arguments.of(RequestType.DESIGN_CHANGE_WITH_VVB, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("allowsMethodologiesTestCases")
  void allowsMethodologies(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).allowsMethodologies());
  }

  static Stream<Arguments> allowsCreateCreditingPeriodsTestCases() {
    return Stream.of(
        Arguments.of(RequestType.DESIGN_CERTIFICATION_RENEWAL_PROJECT, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("allowsCreateCreditingPeriodsTestCases")
  void allowsCreateCreditingPeriods(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).allowsCreateCreditingPeriods());
  }

  static Stream<Arguments> allowsCreateMonitoringPeriodTestCases() {
    return Stream.of(
        Arguments.of(RequestType.PERFORMANCE_CERTIFICATION_PROJECT, true, "included ID"),
        Arguments.of(RequestType.PRELIMINARY_REVIEW_ID, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("allowsCreateMonitoringPeriodTestCases")
  void allowsCreateMonitoringPeriod(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).allowsCreateMonitoringPeriod());
  }

  static Stream<Arguments> allowsUpdateMonitoringPeriodTestCases() {
    return Stream.of(
        Arguments.of(RequestType.PERFORMANCE_CERTIFICATION_PROJECT, true, "included ID"),
        Arguments.of(RequestType.PRELIMINARY_REVIEW_ID, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("allowsUpdateMonitoringPeriodTestCases")
  void allowsUpdateMonitoringPeriod(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).allowsUpdateMonitoringPeriod());
  }

  static Stream<Arguments> shouldCalculateDesignCertificationDateTestCases() {
    return Stream.of(
        Arguments.of(RequestType.DESIGN_CERTIFICATION_PROJECT, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("shouldCalculateDesignCertificationDateTestCases")
  void shouldCalculateDesignCertificationDate(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).shouldCalculateDesignCertificationDate());
  }

  static Stream<Arguments> supportsPAATestCases() {
    return Stream.of(
        Arguments.of(RequestType.DESIGN_CHANGE_WITH_VVB, true, "included ID"),
        Arguments.of(RequestType.GENERIC, false, "excluded ID"),
        Arguments.of(-1, false, "invalid ID"));
  }

  @ParameterizedTest(name = "{index}: {2} ({0}) → expected={1}")
  @MethodSource("supportsPAATestCases")
  void supportsPAA(Integer id, Boolean expected, String description) {
    assertEquals(expected, typeWithId(id).supportsPAA());
  }

  @Test
  void supportsPAADoesNotThrowWhenIdIsNull() {
    assertDoesNotThrow(new RequestType()::supportsPAA);
  }

  private RequestType typeWithId(Integer id) {
    return RequestTypeMother.withId(id);
  }
}
