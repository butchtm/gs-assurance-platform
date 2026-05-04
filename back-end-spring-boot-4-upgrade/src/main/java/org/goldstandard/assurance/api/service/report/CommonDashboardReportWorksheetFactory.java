package org.goldstandard.assurance.api.service.report;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@RequiredArgsConstructor
@Slf4j
public class CommonDashboardReportWorksheetFactory {

  private final Collection<String> columnHeaders;

  public CommonDashboardReportWorksheetFactory() {
    this(buildColumnHeaders());
  }

  public SXSSFSheet buildWorksheet(SXSSFWorkbook workbook) {
    var worksheet = workbook.createSheet("Common Dashboard Report");
    var numberOfColumns = columnHeaders.size();
    IntStream.range(0, numberOfColumns).forEach(worksheet::trackColumnForAutoSizing);
    worksheet.setDefaultColumnStyle(6, createDateStyle(workbook));
    worksheet.setDefaultColumnStyle(7, createDateStyle(workbook));
    worksheet.setDefaultColumnStyle(8, createNumberStyle(workbook));
    worksheet.setDefaultColumnStyle(9, createDateStyle(workbook));
    writeHeader(worksheet);
    return worksheet;
  }

  public void autoSizeAllColumns(SXSSFSheet worksheet) {
    var numberOfColumns = columnHeaders.size();
    IntStream.range(0, numberOfColumns).forEach(worksheet::autoSizeColumn);
  }

  private void writeHeader(SXSSFSheet worksheet) {
    var row = worksheet.createRow(0);
    int columnIndex = 0;
    for (String columnHeader : columnHeaders) {
      var cell = row.createCell(columnIndex);
      cell.setCellValue(columnHeader);
      columnIndex++;
    }
  }

  private static Collection<String> buildColumnHeaders() {
    return List.of(
        "GSID",
        "Project Name",
        "Request Status",
        "Request",
        "PoA GSID",
        "Real Case GSID",
        "First RoR Start Date",
        "Current RoR End Date",
        "RoR Count",
        "Request Closed Date",
        "Project Classification",
        "Project Types",
        "Invoice Status",
        "Request ID",
        "Project Fee Schedule",
        "Monitoring Period Fee Schedule",
        "Account Name");
  }

  private static CellStyle createNumberStyle(SXSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    var numberFormat = workbook.createDataFormat();
    style.setDataFormat(numberFormat.getFormat("0"));
    return style;
  }

  private static CellStyle createDateStyle(SXSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    var dateFormat = workbook.createDataFormat();
    style.setDataFormat(dateFormat.getFormat("dd/MM/yyyy"));
    return style;
  }
}
