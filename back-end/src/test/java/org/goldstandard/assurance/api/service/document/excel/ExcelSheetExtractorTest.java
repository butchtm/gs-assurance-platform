package org.goldstandard.assurance.api.service.document.excel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExcelSheetExtractorTest {

  private final ExcelSheetExtractor sheetExtractor = new ExcelSheetExtractor();

  @Test
  void shouldExtractSheetsFromWorkbook() {
    var workbook = mock(Workbook.class);
    var sheet1 = mock(Sheet.class);
    var sheet2 = mock(Sheet.class);
    when(workbook.getNumberOfSheets()).thenReturn(2);
    when(workbook.getSheetAt(0)).thenReturn(sheet1);
    when(workbook.getSheetAt(1)).thenReturn(sheet2);

    var extractedSheets = sheetExtractor.extractSheets(workbook);

    assertThat(extractedSheets).contains(sheet1, sheet2);
  }
}
