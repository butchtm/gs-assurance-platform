package org.goldstandard.assurance.api.service.report;

import static org.goldstandard.assurance.api.service.report.ReportUtils.numberCell;
import static org.goldstandard.assurance.api.service.report.ReportUtils.stringCell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.goldstandard.assurance.api.dto.AssignmentSummaryDTO;

@RequiredArgsConstructor
@Slf4j
public class RoeAssignmentSummaryReportRowPopulator implements AssignmentSummaryReportRowPopulator {

  public void populateSummaryRow(SXSSFRow row, AssignmentSummaryDTO summary) {
    int columnIndex = 0;
    stringCell(row, columnIndex++, summary.getAccountName());
    numberCell(row, columnIndex++, summary.getRoundOneRequests());
    numberCell(row, columnIndex++, summary.getInProgressRequests());
    numberCell(row, columnIndex++, summary.getOpenRequests());
    numberCell(row, columnIndex, summary.getClosedRequests());
  }

  public void populateTotalRow(SXSSFRow row, AssignmentSummaryDTO totals) {
    int columnIndex = 0;
    stringCell(row, columnIndex++, "Totals");
    numberCell(row, columnIndex++, totals.getRoundOneRequests());
    numberCell(row, columnIndex++, totals.getInProgressRequests());
    numberCell(row, columnIndex++, totals.getOpenRequests());
    numberCell(row, columnIndex, totals.getClosedRequests());
  }
}
