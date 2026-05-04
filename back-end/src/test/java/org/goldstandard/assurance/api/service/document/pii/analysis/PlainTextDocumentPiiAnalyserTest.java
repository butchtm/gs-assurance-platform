package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.goldstandard.assurance.api.dto.ExtractedText;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.document.DocumentStoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class PlainTextDocumentPiiAnalyserTest {

  @InjectMocks private PlainTextDocumentPiiAnalyser analyser;
  @Mock private ComprehendService comprehendService;
  @Mock private DocumentStoreService documentStoreService;
  @Mock private PlainTextDocumentChunker textChunker;
  @Captor private ArgumentCaptor<Collection<ExtractedText>> extractedTextCaptor;

  private static final String BUCKET = "bucket";
  private static final String S3_KEY = "document1.txt";

  private static final DocumentPiiAnalysisParams PARAMS =
      DocumentPiiAnalysisParams.builder().bucketName(BUCKET).s3Key(S3_KEY).build();

  @Test
  void shouldAnalyseTextDocument() {
    var textContent = "This is a test document with some PII data.";
    var inputStream = new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8));
    var extractedTextList =
        List.of(ExtractedText.builder().pageNumber(1).text(textContent).build());
    var probablePii = List.of(ProbablePii.builder().entity("ADDRESS").pageNumber(1).build());

    when(documentStoreService.retrieveDocumentAsStream(S3_KEY)).thenReturn(inputStream);
    when(textChunker.chunkTextIntoPages(textContent)).thenReturn(extractedTextList);
    when(comprehendService.detectDocumentEntities(extractedTextList)).thenReturn(probablePii);

    var result = analyser.analyse(PARAMS);

    assertThat(result).isEqualTo(probablePii);
    verify(textChunker).chunkTextIntoPages(textContent);
    verify(comprehendService).detectDocumentEntities(extractedTextList);
  }

  @Test
  void shouldReturnEmptyListForEmptyDocument() {
    var emptyContent = "";
    var inputStream = new ByteArrayInputStream(emptyContent.getBytes(StandardCharsets.UTF_8));

    when(documentStoreService.retrieveDocumentAsStream(S3_KEY)).thenReturn(inputStream);

    var result = analyser.analyse(PARAMS);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmptyListForWhitespaceOnlyDocument() {
    var whitespaceContent = "   \n\t  ";
    var inputStream = new ByteArrayInputStream(whitespaceContent.getBytes(StandardCharsets.UTF_8));

    when(documentStoreService.retrieveDocumentAsStream(S3_KEY)).thenReturn(inputStream);

    var result = analyser.analyse(PARAMS);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldPassCorrectTextContentToChunker() {
    var textContent = "Sample text content for chunking test";
    var inputStream = new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8));
    var extractedTextList =
        List.of(ExtractedText.builder().pageNumber(1).text(textContent).build());

    when(documentStoreService.retrieveDocumentAsStream(S3_KEY)).thenReturn(inputStream);
    when(textChunker.chunkTextIntoPages(textContent)).thenReturn(extractedTextList);
    when(comprehendService.detectDocumentEntities(any())).thenReturn(List.of());

    analyser.analyse(PARAMS);

    var textCaptor = ArgumentCaptor.forClass(String.class);
    verify(textChunker).chunkTextIntoPages(textCaptor.capture());
    assertThat(textCaptor.getValue()).isEqualTo(textContent);
  }

  @Test
  void shouldPassChunkedTextToComprehendService() {
    var textContent = "Test content";
    var inputStream = new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8));
    var extractedText =
        List.of(
            ExtractedText.builder().pageNumber(1).text("chunk1").build(),
            ExtractedText.builder().pageNumber(2).text("chunk2").build());

    when(documentStoreService.retrieveDocumentAsStream(S3_KEY)).thenReturn(inputStream);
    when(textChunker.chunkTextIntoPages(textContent)).thenReturn(extractedText);
    when(comprehendService.detectDocumentEntities(anyList())).thenReturn(List.of());

    analyser.analyse(PARAMS);

    verify(comprehendService).detectDocumentEntities(extractedTextCaptor.capture());
    assertThat(extractedTextCaptor.getValue()).isEqualTo(extractedText);
  }

  @Test
  void shouldSupportPlainTextDocumentsUnderOrEqualTo50Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .fileSizeMb(50L)
            .build();
    assertThat(analyser.supported(criteria)).isTrue();
  }

  @Test
  void shouldNotSupportPlainTextDocumentsOver50Mb() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .fileSizeMb(51L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }

  @Test
  void shouldNotSupportNonPlainTextDocuments() {
    var criteria =
        DocumentPiiScanCriteria.builder()
            .contentType(MediaType.APPLICATION_PDF_VALUE)
            .fileSizeMb(10L)
            .build();
    assertThat(analyser.supported(criteria)).isFalse();
  }
}
