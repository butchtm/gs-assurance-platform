package org.goldstandard.assurance.api.service.report;

import static org.goldstandard.assurance.api.service.report.ReportUtils.numberCell;
import static org.goldstandard.assurance.api.service.report.ReportUtils.stringCell;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;

public class ArmAssignmentSummaryReportRowPopulator implements AssignmentSummaryReportRowPopulator {

  public void populateSummaryRow(SXSSFRow row, AssignmentSummaryDTO summary) {
    int columnIndex = 0;
    stringCell(row, columnIndex++, summary.getAccountName());
    numberCell(row, columnIndex++, summary.getUpcomingRequests());
    numberCell(row, columnIndex++, summary.getDueRequests());
    numberCell(row, columnIndex, summary.getOverdueRequests());
  }

  public void populateTotalRow(SXSSFRow row, AssignmentSummaryDTO totals) {
    int columnIndex = 0;
    stringCell(row, columnIndex++, "Totals");
    numberCell(row, columnIndex++, totals.getUpcomingRequests());
    numberCell(row, columnIndex++, totals.getDueRequests());
    numberCell(row, columnIndex, totals.getOverdueRequests());
  }
}
