package org.goldstandard.assurance.api.service.activity;

import static org.goldstandard.assurance.api.entity.activity.Pathway.*;
import static org.goldstandard.assurance.api.entity.activity.PathwayGroup.*;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.AssignmentType;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.service.AssignmentFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PathwayIndexCalculator {

  @RequiredArgsConstructor
  @Getter
  public static class PathwayIndexes {
    private final int pathwayIndex;
    private final int roundTwoPathwayGroupIndex;
  }

  private final AssignmentFinder assignmentFinder;

  public Optional<PathwayIndexes> calculatePathwayIndexes(
      Request updatedRequest, RequestStatus originalStatus) {
    if (isCompletenessCheckPathway(updatedRequest, originalStatus)) {
      return Optional.of(new PathwayIndexes(CC_PATHWAY_INDEX, CC_ROUND_2_PLUS_PG_INDEX));
    }
    if (updatedRequest.isFastTracked()) {
      return Optional.of(
          new PathwayIndexes(FAST_TRACK_PATHWAY_INDEX, FAST_TRACK_ROUND_2_PLUS_PG_INDEX));
    }
    return calculateInternalOrExternal(updatedRequest);
  }

  private boolean isCompletenessCheckPathway(Request updatedRequest, RequestStatus originalStatus) {
    return updatedRequest.getCurrentRequestStatus().isInCompletenessCheck()
        && (originalStatus.isInPreparation() || originalStatus.isInAudit());
  }

  private Optional<PathwayIndexes> calculateInternalOrExternal(Request request) {
    if (!request.hasInternal()) {
      return Optional.empty();
    }
    if (request.isInternal()) {
      return Optional.of(calculateInternalPathway(request));
    }
    return Optional.of(calculateExternalPathway(request));
  }

  private PathwayIndexes calculateInternalPathway(Request request) {
    var type = request.getRequestType();
    if (type.isOneOfPerformanceCertification()) {
      return new PathwayIndexes(
          PERFORMANCE_CERTIFICATION_INTERNAL_PATHWAY_INDEX,
          PERFORMANCE_INTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    if (type.isInclusionRegularCase()) {
      return calculateInclusionRegularCasePathway(request);
    }
    if (type.isOneOfCombinedDesignAndPerformanceCertification()) {
      return new PathwayIndexes(
          COMBINED_INTERNAL_PATHWAY_INDEX, COMBINED_INTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    return new PathwayIndexes(QAQC_INTERNAL_PATHWAY_INDEX, QAQC_INTERNAL_ROUND_2_PLUS_PG_INDEX);
  }

  private PathwayIndexes calculateInclusionRegularCasePathway(Request request) {
    if (hasAuditAssignment(request)) {
      return new PathwayIndexes(
          INCLUSION_INTERNAL_PATHWAY_INDEX, INCLUSION_INTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    return new PathwayIndexes(QAQC_INTERNAL_PATHWAY_INDEX, QAQC_INTERNAL_ROUND_2_PLUS_PG_INDEX);
  }

  private PathwayIndexes calculateExternalPathway(Request request) {
    var type = request.getRequestType();
    if (type.isOneOfPerformanceCertification()) {
      return new PathwayIndexes(
          PERFORMANCE_CERTIFICATION_EXTERNAL_PATHWAY_INDEX,
          PERFORMANCE_EXTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    if (type.isInclusionRegularCase()) {
      if (hasAuditAssignment(request)) {
        return new PathwayIndexes(
            INCLUSION_EXTERNAL_PATHWAY_INDEX, INCLUSION_EXTERNAL_ROUND_2_PLUS_PG_INDEX);
      }
      return new PathwayIndexes(ROE_EXTERNAL_PATHWAY_INDEX, ROE_EXTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    if (type.isOneOfCombinedDesignAndPerformanceCertification()) {
      return new PathwayIndexes(
          COMBINED_EXTERNAL_PATHWAY_INDEX, COMBINED_EXTERNAL_ROUND_2_PLUS_PG_INDEX);
    }
    return new PathwayIndexes(ROE_EXTERNAL_PATHWAY_INDEX, ROE_EXTERNAL_ROUND_2_PLUS_PG_INDEX);
  }

  private boolean hasAuditAssignment(Request request) {
    return assignmentFinder.hasAssignmentOfType(
        request.getId(), AssignmentType.AUDIT_ASSIGNMENT_TYPE_ID);
  }
}
