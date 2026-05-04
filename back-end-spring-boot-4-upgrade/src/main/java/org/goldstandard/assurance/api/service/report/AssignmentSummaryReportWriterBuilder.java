package org.goldstandard.assurance.api.service.report;

import org.springframework.stereotype.Component;

@Component
public class AssignmentSummaryReportWriterBuilder {

  public AssignmentSummaryReportWriter buildWriter(AssignmentSummaryReportWriterParams params) {
    return new AssignmentSummaryReportWriter(
        params.getSummaryFinder(),
        params.getFactory(),
        params.getFilters(),
        params.getOriginalPageable());
  }
}
