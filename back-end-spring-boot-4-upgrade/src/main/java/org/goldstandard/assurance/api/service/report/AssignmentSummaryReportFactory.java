package org.goldstandard.assurance.api.service.report;

import org.goldstandard.assurance.api.enumeration.AssignmentSummaryType;

public interface AssignmentSummaryReportFactory {

  AssignmentSummaryReportWorksheetFactory worksheetFactory();

  AssignmentSummaryReportRowPopulator rowPopulator();

  AssignmentSummaryType getType();
}
