package org.goldstandard.assurance.api.service.report;

import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.springframework.stereotype.Component;

@Component
public class RoeAssignmentSummaryReportFactory implements AssignmentSummaryReportFactory {

  public AssignmentSummaryReportWorksheetFactory worksheetFactory() {
    return new RoeAssignmentSummaryReportWorksheetFactory();
  }

  public AssignmentSummaryReportRowPopulator rowPopulator() {
    return new RoeAssignmentSummaryReportRowPopulator();
  }

  public AssignmentSummaryType getType() {
    return AssignmentSummaryType.ROE;
  }
}
