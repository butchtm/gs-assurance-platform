package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.util.DocumentUtils.EXCEL_XLSX;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.fixtures.TextractObjectMother;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.document.DocumentStoreService;
import org.goldstandard.assurance.api.service.document.excel.ExcelSheetExtractor;
import org.goldstandard.assurance.api.service.document.excel.ExcelTextExtractor;
import org.goldstandard.assurance.api.service.document.excel.ExcelWorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ExcelDocumentPiiAnalyserTest {

  @InjectMocks private ExcelDocumentPiiAnalyser analyser;
  @Mock private DocumentStoreService documentStoreService;
  @Mock private ExcelWorkbookFactory workbookFactory;
  @Mock private ExcelSheetExtractor sheetExtractor;
  @Mock private ExcelTextExtractor textExtractor;
  @Mock private ComprehendService comprehendService;

  private static final String DOC_NAME = "document1.pdf";

  private static final DocumentPiiAnalysisParams PARAMS =
      DocumentPiiAnalysisParams.builder().s3Key(DOC_NAME).build();

  @Test
  void shouldExtractAndDetectEntities() throws IOException {
    var extractedWords = TextractObjectMother.resultTexts();
    var probablePii = List.of(ProbablePii.builder().entity("ADDRESS").pageNumber(2).build());
    var documentStream = mock(InputStream.class);
    var workbook = mock(Workbook.class);
    var sheet = mock(Sheet.class);

    when(documentStoreService.retrieveDocumentAsStream(DOC_NAME)).thenReturn(documentStream);
    when(workbookFactory.create(documentStream)).thenReturn(workbook);
    when(sheetExtractor.extractSheets(workbook)).thenReturn(Collections.singletonList(sheet));
    when(textExtractor.extractTextChunked(eq(sheet), anyInt())).thenReturn(extractedWords);
    when(comprehendService.detectDocumentEntities(extractedWords)).thenReturn(probablePii);

    assertThat(analyser.analyse(PARAMS)).isEqualTo(probablePii);

    verify(workbookFactory).create(documentStream);
    verify(sheetExtractor).extractSheets(workbook);
    verify(textExtractor).extractTextChunked(eq(sheet), anyInt());
    verify(comprehendService).detectDocumentEntities(extractedWords);
  }

  @Test
  void shouldSupportExcelDocuments() {
    var criteria = DocumentPiiScanCriteria.builder().contentType(EXCEL_XLSX).build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportNonExcelDocuments() {
    var criteria =
        DocumentPiiScanCriteria.builder().contentType(MediaType.APPLICATION_PDF_VALUE).build();
    assertThat(analyser.supported(criteria)).isFalse();
  }
}
