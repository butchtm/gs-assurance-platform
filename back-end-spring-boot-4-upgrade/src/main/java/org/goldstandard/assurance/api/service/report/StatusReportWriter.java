package org.goldstandard.assurance.api.service.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.goldstandard.assurance.api.entity.StatusReport;

@RequiredArgsConstructor
@Slf4j
public class StatusReportWriter {

  private final StatusReport statusReport;

  public byte[] writeToByteArray() throws IOException {
    try (var workbook = new SXSSFWorkbook();
        var outputStream = new ByteArrayOutputStream()) {

      var worksheetFactory = new StatusReportWorksheetFactory();
      var rowPopulator = new StatusReportRowPopulator(statusReport);

      var projectsWorksheet = worksheetFactory.buildProjectsByTypeWorksheet(workbook);
      rowPopulator.populateProjectsByTypeWorksheet(projectsWorksheet);
      worksheetFactory.autoSizeAllColumns(projectsWorksheet);

      var requestsWorksheet = worksheetFactory.buildRequestsByTypeWorksheet(workbook);
      rowPopulator.populateRequestsByTypeWorksheet(requestsWorksheet);
      worksheetFactory.autoSizeAllColumns(requestsWorksheet);

      var summaryWorksheet = worksheetFactory.buildSummaryWorksheet(workbook);
      rowPopulator.populateSummaryWorksheet(summaryWorksheet);
      worksheetFactory.autoSizeAllColumns(summaryWorksheet);

      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }
}
