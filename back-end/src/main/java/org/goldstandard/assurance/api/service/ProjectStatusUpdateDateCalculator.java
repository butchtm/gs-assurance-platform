package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.DesignCertDateMatrixRepository;
import org.goldstandard.assurance.api.repository.system.RequestStatusHistoryRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectStatusUpdateDateCalculator {

  private final DesignCertDateMatrixRepository designCertDateMatrixRepository;
  private final AssignmentFinder assignmentFinder;
  private final RequestStatusHistoryRepository historyRepository;
  private final Clock clock;

  public ZonedDateTime calculateProjectStatusUpdateDate(Request request) {
    if (request.shouldCalculateDesignCertificationDate()) {
      return calculateDesignCertificationDate(request);
    }
    return ZonedDateTime.now(clock);
  }

  private ZonedDateTime calculateDesignCertificationDate(Request request) {
    var firstInProgressDate = getFirstInProgressDate(request);
    var daysToAdd = calculateDesignCertificationDaysToAdd(request);
    return firstInProgressDate.plusDays(daysToAdd);
  }

  private Integer calculateDesignCertificationDaysToAdd(Request request) {
    var auditAssignment = assignmentFinder.hasAuditAssignment(request.getId());
    var requestType = request.getRequestType();
    return designCertDateMatrixRepository
        .findDaysToAdd(requestType.getId(), auditAssignment)
        .orElseThrow();
  }

  private ZonedDateTime getFirstInProgressDate(Request request) {
    return historyRepository.getFirstInProgressDate(request.getId()).orElseThrow();
  }
}
