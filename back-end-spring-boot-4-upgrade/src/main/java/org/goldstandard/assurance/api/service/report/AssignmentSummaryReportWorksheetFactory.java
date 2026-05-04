package org.goldstandard.assurance.api.service.report;

import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface AssignmentSummaryReportWorksheetFactory {
  SXSSFSheet buildWorkSheet(SXSSFWorkbook workbook);

  void autoSizeAllColumns(SXSSFSheet worksheet);
}
