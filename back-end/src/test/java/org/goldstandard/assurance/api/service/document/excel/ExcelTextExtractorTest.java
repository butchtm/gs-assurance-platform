package org.goldstandard.assurance.api.service.document.excel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.goldstandard.assurance.api.util.Offsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExcelTextExtractorTest {

  private final ExcelTextExtractor textExtractor = new ExcelTextExtractor();

  @Mock private Sheet sheet;

  private static final String BASIC_SHEET_STRING =
      "A1contents |END| A2contents |END| B1contents |END| B2contents |END| ";

  @Test
  void shouldExtractTextWithOffsets() {
    setupBasicSheet();

    var extractedTextList = textExtractor.extractTextChunked(sheet, 100);

    assertThat(extractedTextList).hasSize(1);
    var extractedText = extractedTextList.getFirst();
    assertThat(extractedText.getText()).isEqualTo(BASIC_SHEET_STRING);
    var sheetLocations = extractedText.getSheetLocations();
    assertThat(sheetLocations)
        .hasSize(4)
        .containsEntry(new Offsets(0, 17), "sheet1!A1")
        .containsEntry(new Offsets(17, 34), "sheet1!B1")
        .containsEntry(new Offsets(34, 51), "sheet1!A2")
        .containsEntry(new Offsets(51, 68), "sheet1!B2");
  }

  @Test
  void shouldCreateSeparateExtractedTextWhenChunkSizeWouldBeExceeded() {
    setupBasicSheet();

    var extractedTextList = textExtractor.extractTextChunked(sheet, 20);

    assertThat(extractedTextList).hasSize(4);
    var firstExtractedText = extractedTextList.getFirst();
    assertThat(firstExtractedText.getText()).isEqualTo("A1contents |END| ");
    var sheetLocations = firstExtractedText.getSheetLocations();
    assertThat(sheetLocations).hasSize(1).containsEntry(new Offsets(0, 17), "sheet1!A1");
  }

  private void setupBasicSheet() {
    var row1Cell1 = Mockito.mock(Cell.class);
    when(row1Cell1.toString()).thenReturn("A1contents");
    when(row1Cell1.getRowIndex()).thenReturn(0);
    when(row1Cell1.getColumnIndex()).thenReturn(0);
    when(row1Cell1.getSheet()).thenReturn(sheet);
    var row1Cell2 = Mockito.mock(Cell.class);
    when(row1Cell2.toString()).thenReturn("A2contents");
    when(row1Cell2.getRowIndex()).thenReturn(0);
    when(row1Cell2.getColumnIndex()).thenReturn(1);
    when(row1Cell2.getSheet()).thenReturn(sheet);
    var row1Iterator = Arrays.asList(row1Cell1, row1Cell2).iterator();

    var row2Cell1 = Mockito.mock(Cell.class);
    when(row2Cell1.toString()).thenReturn("B1contents");
    when(row2Cell1.getRowIndex()).thenReturn(1);
    when(row2Cell1.getColumnIndex()).thenReturn(0);
    when(row2Cell1.getSheet()).thenReturn(sheet);
    var row2Cell2 = Mockito.mock(Cell.class);
    when(row2Cell2.toString()).thenReturn("B2contents");
    when(row2Cell2.getRowIndex()).thenReturn(1);
    when(row2Cell2.getColumnIndex()).thenReturn(1);
    when(row2Cell2.getSheet()).thenReturn(sheet);
    var row2Iterator = Arrays.asList(row2Cell1, row2Cell2).iterator();

    var row1 = Mockito.mock(Row.class);
    var row2 = Mockito.mock(Row.class);
    when(row1.iterator()).thenReturn(row1Iterator);
    when(row2.iterator()).thenReturn(row2Iterator);
    var rowIterator = Arrays.asList(row1, row2).iterator();

    when(sheet.iterator()).thenReturn(rowIterator);
    when(sheet.getSheetName()).thenReturn("sheet1");
  }
}
