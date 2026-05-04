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
public class StatusReportWorksheetFactory {

  private static final String DESCRIPTION_HEADER = "Description";
  private static final String COUNT_HEADER = "Count";
  private static final String TYPE_HEADER = "Type";
  private static final Integer COUNT_COLUMN = 2;

  public SXSSFSheet buildProjectsByTypeWorksheet(SXSSFWorkbook workbook) {
    return buildWorksheet(workbook, "Projects by Type");
  }

  public SXSSFSheet buildRequestsByTypeWorksheet(SXSSFWorkbook workbook) {
    return buildWorksheet(workbook, "Requests by Type");
  }

  public SXSSFSheet buildSummaryWorksheet(SXSSFWorkbook workbook) {
    return buildWorksheet(workbook, "Summary");
  }

  private SXSSFSheet buildWorksheet(SXSSFWorkbook workbook, String sheetName) {
    var worksheet = workbook.createSheet(sheetName);
    var columnHeaders = buildColumnHeaders();
    var numberOfColumns = columnHeaders.size();
    IntStream.range(0, numberOfColumns).forEach(worksheet::trackColumnForAutoSizing);
    worksheet.setDefaultColumnStyle(COUNT_COLUMN, createNumberStyle(workbook));
    writeHeader(worksheet, columnHeaders);
    return worksheet;
  }

  public void autoSizeAllColumns(SXSSFSheet worksheet) {
    var numberOfColumns = worksheet.getRow(0).getLastCellNum();
    IntStream.range(0, numberOfColumns).forEach(worksheet::autoSizeColumn);
  }

  private void writeHeader(SXSSFSheet worksheet, Collection<String> columnHeaders) {
    var row = worksheet.createRow(0);
    int columnIndex = 0;
    for (String columnHeader : columnHeaders) {
      var cell = row.createCell(columnIndex);
      cell.setCellValue(columnHeader);
      columnIndex++;
    }
  }

  private static Collection<String> buildColumnHeaders() {
    return List.of(TYPE_HEADER, DESCRIPTION_HEADER, COUNT_HEADER);
  }

  private static CellStyle createNumberStyle(SXSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    var numberFormat = workbook.createDataFormat();
    style.setDataFormat(numberFormat.getFormat("0"));
    return style;
  }
}
