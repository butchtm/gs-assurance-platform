package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.fixtures.TextractObjectMother;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.textract.TextractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class TextractDocumentPiiAnalyserTest {

  @InjectMocks private TextractDocumentPiiAnalyser analyser;
  @Mock private TextractService textractService;
  @Mock private ExtractedTextS3Writer s3Writer;
  @Mock private ComprehendService comprehendService;
  @Mock private DocumentPiiDbService documentPiiDbService;

  @Captor private ArgumentCaptor<String> s3FolderNameCaptor;

  private static final String BUCKET = "bucket";
  private static final String DOC_NAME = "document1.pdf";
  private static final String JOB_ID = "job1";
  private static final UUID DOC_ID = java.util.UUID.randomUUID();

  private static final DocumentPiiAnalysisParams PARAMS =
      DocumentPiiAnalysisParams.builder()
          .bucketName(BUCKET)
          .s3Key(DOC_NAME)
          .documentName(DOC_NAME)
          .documentId(DOC_ID)
          .build();

  @Test
  void shouldExtractAndStartPiiDetectionJobIfTextFound() {
    var extractedWords = TextractObjectMother.resultTexts();
    var expectedResult = AnalysisStartResult.builder().build();

    when(textractService.doDetectTextJob(BUCKET, DOC_NAME)).thenReturn(JOB_ID);
    when(textractService.extractWords(JOB_ID)).thenReturn(Stream.of(extractedWords));

    when(comprehendService.detectDocumentEntitiesAsync(anyString())).thenReturn(expectedResult);

    assertThat(analyser.startComprehendAnalysis(PARAMS)).isEqualTo(expectedResult);

    verify(documentPiiDbService).markTextractAsFinished(DOC_ID);
    verify(textractService).doDetectTextJob(BUCKET, DOC_NAME);
    verify(textractService).extractWords(JOB_ID);
    verify(s3Writer).writePagesToS3(eq(extractedWords), s3FolderNameCaptor.capture());
    verify(comprehendService).detectDocumentEntitiesAsync(s3FolderNameCaptor.getValue());
  }

  @Test
  void shouldExtractAndReturnNoTextFoundResultIfTextractFoundNoText() {
    when(textractService.doDetectTextJob(BUCKET, DOC_NAME)).thenReturn(JOB_ID);
    when(textractService.extractWords(JOB_ID)).thenReturn(Stream.of(Collections.emptyList()));

    assertThat(analyser.startComprehendAnalysis(PARAMS).isTextFound()).isFalse();

    verify(textractService).doDetectTextJob(BUCKET, DOC_NAME);
    verify(textractService).extractWords(JOB_ID);
    verifyNoInteractions(s3Writer, comprehendService);
  }

  @Test
  void shouldSupportPdfDocumentsUnderOrEqualTo500Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.APPLICATION_PDF_VALUE)
            .fileSizeMb(500L)
            .build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportPdfDocumentsOver500Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.APPLICATION_PDF_VALUE)
            .fileSizeMb(501L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldSupportTiffDocumentsUnderOrEqualTo500Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder().contentType("image/tiff").fileSizeMb(500L).build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportTiffDocumentsOver500Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder().contentType("image/tiff").fileSizeMb(501L).build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldSupportJpegDocumentsUnderOrEqualTo10Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.IMAGE_JPEG_VALUE)
            .fileSizeMb(10L)
            .build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportJpegDocumentsOver10Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.IMAGE_JPEG_VALUE)
            .fileSizeMb(11L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldSupportPngDocumentsUnderOrEqualTo10Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.IMAGE_PNG_VALUE)
            .fileSizeMb(10L)
            .build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportPngDocumentsOver10Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.IMAGE_PNG_VALUE)
            .fileSizeMb(11L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldNotSupportNonTextractSupportedDocuments() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            .fileSizeMb(499L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldReturnTrueForSupportingAsyncComprehend() {
    assertThat(analyser.supportsAsyncComprehend()).isTrue();
  }
}
