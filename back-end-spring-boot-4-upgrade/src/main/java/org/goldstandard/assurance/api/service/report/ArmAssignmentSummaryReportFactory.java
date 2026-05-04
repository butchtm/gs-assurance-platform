package org.goldstandard.assurance.api.service.report;

import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;
import org.springframework.stereotype.Component;

@Component
public class ArmAssignmentSummaryReportFactory implements AssignmentSummaryReportFactory {

  public AssignmentSummaryReportWorksheetFactory worksheetFactory() {
    return new ArmAssignmentSummaryReportWorksheetFactory();
  }

  public AssignmentSummaryReportRowPopulator rowPopulator() {
    return new ArmAssignmentSummaryReportRowPopulator();
  }

  public AssignmentSummaryType getType() {
    return AssignmentSummaryType.ARM;
  }
}
