package org.goldstandard.assurance.api.service.report;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;

public interface AssignmentSummaryReportRowPopulator {
  void populateSummaryRow(SXSSFRow row, AssignmentSummaryDTO summary);

  void populateTotalRow(SXSSFRow row, AssignmentSummaryDTO totals);
}
