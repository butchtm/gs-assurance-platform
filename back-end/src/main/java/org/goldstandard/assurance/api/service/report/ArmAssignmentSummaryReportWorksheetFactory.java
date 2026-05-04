package org.goldstandard.assurance.api.service.report;

import static org.goldstandard.assurance.api.service.report.ReportUtils.createNumberStyle;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@RequiredArgsConstructor
@Slf4j
public class ArmAssignmentSummaryReportWorksheetFactory
    implements AssignmentSummaryReportWorksheetFactory {
  private final Collection<String> columnHeaders;

  public ArmAssignmentSummaryReportWorksheetFactory() {
    this(buildColumnHeaders());
  }

  public SXSSFSheet buildWorkSheet(SXSSFWorkbook workbook) {
    var worksheet = workbook.createSheet("Assignment Summary Report");
    var numberOfColumns = columnHeaders.size();
    IntStream.range(0, numberOfColumns).forEach(worksheet::trackColumnForAutoSizing);
    worksheet.setDefaultColumnStyle(2, createNumberStyle(workbook));
    worksheet.setDefaultColumnStyle(3, createNumberStyle(workbook));
    worksheet.setDefaultColumnStyle(4, createNumberStyle(workbook));
    writeHeader(worksheet);
    return worksheet;
  }

  public void autoSizeAllColumns(SXSSFSheet worksheet) {
    ReportUtils.autoSizeAllColumns(worksheet, columnHeaders);
  }

  private void writeHeader(SXSSFSheet worksheet) {
    ReportUtils.writeHeader(worksheet, columnHeaders);
  }

  private static Collection<String> buildColumnHeaders() {
    return List.of("Name", "Upcoming Requests", "No. Of Requests Due", "No. Of Overdue Requests");
  }
}
