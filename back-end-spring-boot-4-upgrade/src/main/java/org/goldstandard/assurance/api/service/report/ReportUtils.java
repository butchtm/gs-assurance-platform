package org.goldstandard.assurance.api.service.report;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportUtils {

  public static void autoSizeAllColumns(SXSSFSheet worksheet, Collection<String> columnHeaders) {
    var numberOfColumns = columnHeaders.size();
    IntStream.range(0, numberOfColumns).forEach(worksheet::autoSizeColumn);
  }

  public static void writeHeader(SXSSFSheet worksheet, Collection<String> columnHeaders) {
    var row = worksheet.createRow(0);
    int columnIndex = 0;
    for (String columnHeader : columnHeaders) {
      var cell = row.createCell(columnIndex);
      cell.setCellValue(columnHeader);
      columnIndex++;
    }
  }

  public static CellStyle createNumberStyle(SXSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    var numberFormat = workbook.createDataFormat();
    style.setDataFormat(numberFormat.getFormat("0"));
    return style;
  }

  public static void stringCell(SXSSFRow row, int columnIndex, String value) {
    var cell = row.createCell(columnIndex);
    cell.setCellValue(Optional.ofNullable(value).orElse(""));
  }

  public static void numberCell(SXSSFRow row, int columnIndex, Integer value) {
    var cell = row.createCell(columnIndex);
    cell.setCellValue(Optional.ofNullable(value).map(v -> Integer.toString(v)).orElse(""));
  }
}
