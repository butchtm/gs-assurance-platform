package org.goldstandard.assurance.api.service.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.activity.Pathway.*;
import static org.goldstandard.assurance.api.entity.activity.PathwayGroup.*;
import static org.goldstandard.assurance.api.entity.lookup.AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.INCLUSION_REGULAR_VPA;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.LEGACY_PERFORMANCE_REVIEW_ID;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.PERFORMANCE_CERTIFICATION_PROJECT;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.PRELIMINARY_REVIEW_ID;
import static org.mockito.Mockito.when;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathwayIndexCalculatorTest {

  @InjectMocks PathwayIndexCalculator calculator;

  @Mock AssignmentFinder assignmentFinder;

  @Mock Request request;

  @Mock RequestStatus currentStatus;

  @Mock RequestStatus originalStatus;

  @BeforeEach
  void setUp() {
    when(request.getCurrentRequestStatus()).thenReturn(currentStatus);
  }

  @Test
  void
      shouldReturnCompletenessCheckIndexesIfCurrentStatusIsInCompletenessCheckAndOriginalStatusIsInPreparation() {
    when(currentStatus.isInCompletenessCheck()).thenReturn(true);
    when(originalStatus.isInPreparation()).thenReturn(true);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus).orElseThrow();

    assertThat(indexes.getPathwayIndex()).isEqualTo(CC_PATHWAY_INDEX);
    assertThat(indexes.getRoundTwoPathwayGroupIndex()).isEqualTo(CC_ROUND_2_PLUS_PG_INDEX);
  }

  @Test
  void
      shouldReturnCompletenessCheckIndexesIfCurrentStatusIsInCompletenessCheckAndOriginalStatusIsInAudit() {
    when(currentStatus.isInCompletenessCheck()).thenReturn(true);
    when(originalStatus.isInAudit()).thenReturn(true);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus).orElseThrow();

    assertThat(indexes.getPathwayIndex()).isEqualTo(CC_PATHWAY_INDEX);
    assertThat(indexes.getRoundTwoPathwayGroupIndex()).isEqualTo(CC_ROUND_2_PLUS_PG_INDEX);
  }

  @Test
  void
      shouldReturnFastTrackIndexesIfCurrentStatusIsNotInCompletenessCheckAndRequestIsFastTracked() {
    when(currentStatus.isInCompletenessCheck()).thenReturn(false);
    when(request.isFastTracked()).thenReturn(true);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus).orElseThrow();

    assertThat(indexes.getPathwayIndex()).isEqualTo(FAST_TRACK_PATHWAY_INDEX);
    assertThat(indexes.getRoundTwoPathwayGroupIndex()).isEqualTo(FAST_TRACK_ROUND_2_PLUS_PG_INDEX);
  }

  @Test
  void shouldNotReturnIndexesIfNotInCompletenessCheckAndNotFastTrackedAndDoesNotHaveInternal() {
    when(currentStatus.isInCompletenessCheck()).thenReturn(false);
    when(request.isFastTracked()).thenReturn(false);
    when(request.hasInternal()).thenReturn(false);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus);

    assertThat(indexes).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(GetInternalRequestTypeTestCase.class)
  void shouldReturnInternalIndexesIfNotInCompletenessCheckAndNotFastTrackedAndIsInternal(
      GetInternalRequestTypeTestCase testCase) {
    when(currentStatus.isInCompletenessCheck()).thenReturn(false);
    when(request.isFastTracked()).thenReturn(false);
    when(request.hasInternal()).thenReturn(true);
    when(request.isInternal()).thenReturn(true);
    givenRequestTypeOf(testCase.requestTypeId);
    givenAuditAssignment(testCase.auditAssignment);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus).orElseThrow();

    assertThat(indexes.getPathwayIndex()).isEqualTo(testCase.pathwayId);
    assertThat(indexes.getRoundTwoPathwayGroupIndex()).isEqualTo(testCase.pathwayGroupRoundTwoId);
  }

  @ParameterizedTest
  @EnumSource(GetExternalRequestTypeTestCase.class)
  void shouldReturnInternalIndexesIfNotInCompletenessCheckAndNotFastTrackedAndIsExternal(
      GetExternalRequestTypeTestCase testCase) {
    when(currentStatus.isInCompletenessCheck()).thenReturn(false);
    when(request.isFastTracked()).thenReturn(false);
    when(request.hasInternal()).thenReturn(true);
    when(request.isInternal()).thenReturn(false);
    givenRequestTypeOf(testCase.requestTypeId);
    givenAuditAssignment(testCase.auditAssignment);

    var indexes = calculator.calculatePathwayIndexes(request, originalStatus).orElseThrow();

    assertThat(indexes.getPathwayIndex()).isEqualTo(testCase.pathwayId);
    assertThat(indexes.getRoundTwoPathwayGroupIndex()).isEqualTo(testCase.pathwayGroupRoundTwoId);
  }

  @Getter
  @AllArgsConstructor
  public enum GetExternalRequestTypeTestCase {
    ROE(
        PRELIMINARY_REVIEW_ID,
        ROE_EXTERNAL_PATHWAY_INDEX,
        ROE_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    PERFORMANCE(
        PERFORMANCE_CERTIFICATION_PROJECT,
        PERFORMANCE_CERTIFICATION_EXTERNAL_PATHWAY_INDEX,
        PERFORMANCE_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    PERFORMANCE_REVIEW(
        LEGACY_PERFORMANCE_REVIEW_ID,
        PERFORMANCE_CERTIFICATION_EXTERNAL_PATHWAY_INDEX,
        PERFORMANCE_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        null),
    INCLUSION_VVB(
        INCLUSION_REGULAR_VPA,
        INCLUSION_EXTERNAL_PATHWAY_INDEX,
        INCLUSION_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        true),
    INCLUSION(
        INCLUSION_REGULAR_VPA,
        ROE_EXTERNAL_PATHWAY_INDEX,
        ROE_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    COMBINED_DESIGN(
        COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
        COMBINED_EXTERNAL_PATHWAY_INDEX,
        COMBINED_EXTERNAL_ROUND_2_PLUS_PG_INDEX,
        false);
    private final Integer requestTypeId;
    private final Integer pathwayId;
    private final Integer pathwayGroupRoundTwoId;
    private final Boolean auditAssignment;
  }

  @Getter
  @AllArgsConstructor
  public enum GetInternalRequestTypeTestCase {
    ROE(
        PRELIMINARY_REVIEW_ID,
        QAQC_INTERNAL_PATHWAY_INDEX,
        QAQC_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    PERFORMANCE(
        PERFORMANCE_CERTIFICATION_PROJECT,
        PERFORMANCE_CERTIFICATION_INTERNAL_PATHWAY_INDEX,
        PERFORMANCE_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    PERFORMANCE_REVIEW(
        LEGACY_PERFORMANCE_REVIEW_ID,
        PERFORMANCE_CERTIFICATION_INTERNAL_PATHWAY_INDEX,
        PERFORMANCE_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        null),
    INCLUSION_VVB(
        INCLUSION_REGULAR_VPA,
        INCLUSION_INTERNAL_PATHWAY_INDEX,
        INCLUSION_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        true),
    INCLUSION(
        INCLUSION_REGULAR_VPA,
        QAQC_INTERNAL_PATHWAY_INDEX,
        QAQC_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        false),
    COMBINED_DESIGN(
        COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_PROJECT,
        COMBINED_INTERNAL_PATHWAY_INDEX,
        COMBINED_INTERNAL_ROUND_2_PLUS_PG_INDEX,
        false);
    private final Integer requestTypeId;
    private final Integer pathwayId;
    private final Integer pathwayGroupRoundTwoId;
    private final Boolean auditAssignment;
  }

  private void givenRequestTypeOf(Integer requestTypeId) {
    when(request.getRequestType()).thenReturn(new RequestType(requestTypeId));
  }

  private void givenAuditAssignment(Boolean hasAuditAssignment) {
    if (!Boolean.TRUE.equals(hasAuditAssignment)) {
      return;
    }
    when(request.getId()).thenReturn(1);
    when(assignmentFinder.hasAssignmentOfType(request.getId(), AUDIT_ASSIGNMENT_TYPE_ID))
        .thenReturn(true);
  }
}
