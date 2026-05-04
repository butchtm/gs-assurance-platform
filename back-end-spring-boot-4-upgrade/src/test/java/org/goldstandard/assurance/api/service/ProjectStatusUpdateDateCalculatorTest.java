package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.DESIGN_CERTIFICATION_PROJECT;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.DESIGN_CERTIFICATION_REAL_CASE_VPA;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.GENERIC;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.INCLUSION_REGULAR_VPA;
import static org.goldstandard.assurance.api.fixtures.ZonedDateTimeMother.fixed;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.fixtures.RequestMother;
import org.goldstandard.assurance.api.fixtures.RequestTypeMother;
import org.goldstandard.assurance.api.repository.DesignCertDateMatrixRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectStatusUpdateDateCalculatorTest {

  @Mock DesignCertDateMatrixRepository designCertDateMatrixRepository;
  @Mock AssignmentFinder assignmentFinder;
  @Mock RequestStatusHistoryRepository historyRepository;
  @Mock Clock clock;

  @InjectMocks ProjectStatusUpdateDateCalculator calculator;

  @Test
  void shouldCalculateNowIfDesignCertificationDateNotRequired() {
    var request = withRequestTypeId(GENERIC);
    var now = mockTimeNow();

    var result = calculator.calculateProjectStatusUpdateDate(request);

    assertThat(result).isEqualTo(now);
  }

  @ParameterizedTest
  @MethodSource("designCertificationArguments")
  void shouldCalculateDesignCertificationDate(
      Integer requestTypeId, boolean auditAssignment, int daysToAdd) {
    var request = withRequestTypeId(requestTypeId);
    when(assignmentFinder.hasAuditAssignment(request.getId())).thenReturn(auditAssignment);
    when(designCertDateMatrixRepository.findDaysToAdd(requestTypeId, auditAssignment))
        .thenReturn(Optional.of(daysToAdd));
    var inProgressDate = fixed();
    when(historyRepository.getFirstInProgressDate(request.getId()))
        .thenReturn(Optional.of(inProgressDate));

    var result = calculator.calculateProjectStatusUpdateDate(request);

    assertThat(result).isEqualTo(inProgressDate.plusDays(daysToAdd));
  }

  @Test
  void shouldThrowExceptionIfCannotFindFirstInProgressDateOnRequest() {
    var request = withRequestTypeId(DESIGN_CERTIFICATION_PROJECT);
    when(historyRepository.getFirstInProgressDate(request.getId())).thenReturn(Optional.empty());

    var error = catchThrowable(() -> calculator.calculateProjectStatusUpdateDate(request));

    assertThat(error).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldThrowExceptionIfCannotFindDaysToAdd() {
    var request = withRequestTypeId(DESIGN_CERTIFICATION_PROJECT);
    when(historyRepository.getFirstInProgressDate(request.getId()))
        .thenReturn(Optional.of(fixed()));
    when(designCertDateMatrixRepository.findDaysToAdd(DESIGN_CERTIFICATION_PROJECT, false))
        .thenReturn(Optional.empty());

    var error = catchThrowable(() -> calculator.calculateProjectStatusUpdateDate(request));

    assertThat(error).isInstanceOf(NoSuchElementException.class);
  }

  private static Request withRequestTypeId(Integer requestTypeId) {
    return RequestMother.builder().requestType(RequestTypeMother.withId(requestTypeId)).build();
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private static Stream<Arguments> designCertificationArguments() {
    return Stream.of(
        Arguments.of(DESIGN_CERTIFICATION_PROJECT, true, 5),
        Arguments.of(DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA, true, 5),
        Arguments.of(DESIGN_CERTIFICATION_REAL_CASE_VPA, true, 5),
        Arguments.of(COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_POA, true, 5),
        Arguments.of(COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT, true, 5),
        Arguments.of(COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_VPA, true, 5),
        Arguments.of(INCLUSION_REGULAR_VPA, true, 5),
        Arguments.of(INCLUSION_REGULAR_VPA, false, 10));
  }
}
