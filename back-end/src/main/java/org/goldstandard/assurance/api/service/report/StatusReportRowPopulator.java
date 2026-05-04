package org.goldstandard.assurance.api.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.goldstandard.assurance.api.entity.StatusReport;

@RequiredArgsConstructor
@Slf4j
public class StatusReportRowPopulator {

  private final StatusReport statusReport;

  public void populateProjectsByTypeWorksheet(SXSSFSheet worksheet) {
    int rowIndex = 1;

    for (var project : statusReport.getProjectsByType()) {
      var row = worksheet.createRow(rowIndex++);
      row.createCell(0).setCellValue(project.getType());
      row.createCell(1).setCellValue(project.getDescription());
      row.createCell(2).setCellValue(project.getCount());
    }
  }

  public void populateRequestsByTypeWorksheet(SXSSFSheet worksheet) {
    int rowIndex = 1;

    for (var request : statusReport.getCompletedRequests()) {
      var row = worksheet.createRow(rowIndex++);
      row.createCell(0).setCellValue(request.getType());
      row.createCell(1).setCellValue(request.getDescription());
      row.createCell(2).setCellValue(request.getCount());
    }
  }

  public void populateSummaryWorksheet(SXSSFSheet worksheet) {
    int rowIndex = 1;

    var totalCompletedRequests =
        statusReport.getCompletedRequests().stream()
            .mapToInt(request -> request.getCount() != null ? request.getCount() : 0)
            .sum();

    var totalRequestsRow = worksheet.createRow(rowIndex++);
    totalRequestsRow.createCell(0).setCellValue("Summary");
    totalRequestsRow.createCell(1).setCellValue("Total Completed Requests");
    totalRequestsRow.createCell(2).setCellValue(totalCompletedRequests);

    if (statusReport.getSuccessfulCompletenessCheckRequestsCount() != null) {
      var summaryRow = worksheet.createRow(rowIndex);
      summaryRow.createCell(0).setCellValue("Summary");
      summaryRow.createCell(1).setCellValue("Successful Completeness Check Requests");
      summaryRow
          .createCell(2)
          .setCellValue(statusReport.getSuccessfulCompletenessCheckRequestsCount());
    }
  }
}
