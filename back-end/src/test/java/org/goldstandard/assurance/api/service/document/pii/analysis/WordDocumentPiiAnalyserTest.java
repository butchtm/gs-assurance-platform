package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.goldstandard.assurance.api.configuration.S3ConfigurationProperties;
import org.goldstandard.assurance.api.service.document.ConvertibleOutputStream;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.conversion.DocumentConversionServiceDefault;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.jodconverter.core.office.OfficeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WordDocumentPiiAnalyserTest {

  private WordDocumentPiiAnalyser wordAnalyser;
  @Mock private FileStore documentFileStore;
  @Mock private DocumentConversionServiceDefault conversionService;
  @Mock private FileStore scanningFileStore;
  @Mock private S3ConfigurationProperties s3Properties;
  @Mock private TextractDocumentPiiAnalyser textractAnalyser;
  @Captor private ArgumentCaptor<DocumentPiiAnalysisParams> paramsCaptor;

  private static final String BUCKET = "bucket";
  private static final String SCANNING_BUCKET = "piiBucket";
  private static final String S3_KEY = "document1.pdf";
  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  private static final DocumentPiiAnalysisParams PARAMS =
      DocumentPiiAnalysisParams.builder()
          .bucketName(BUCKET)
          .s3Key(S3_KEY)
          .documentId(DOCUMENT_ID)
          .contentType(DocumentUtils.WORD_DOCX)
          .build();

  @BeforeEach
  void setUp() {
    wordAnalyser =
        new WordDocumentPiiAnalyser(
            documentFileStore,
            conversionService,
            scanningFileStore,
            s3Properties,
            textractAnalyser);
  }

  @Test
  void shouldConvertWordToPdfAndUseTextractAnalyser() throws OfficeException {
    var wordInput = mock(ByteArrayInputStream.class);
    var pdfOutput = mock(ConvertibleOutputStream.class);
    var pdfInputStream = mock(ByteArrayInputStream.class);
    var pdfSize = 10;
    var expectedResult = AnalysisStartResult.builder().build();

    when(documentFileStore.getObjectAsStream(S3_KEY)).thenReturn(wordInput);
    when(conversionService.wordToPdf(wordInput, DocumentUtils.WORD_DOCX)).thenReturn(pdfOutput);
    when(pdfOutput.toInputStream()).thenReturn(pdfInputStream);
    when(pdfOutput.size()).thenReturn(pdfSize);
    when(s3Properties.getPiiScanningBucketName()).thenReturn(SCANNING_BUCKET);
    when(textractAnalyser.startComprehendAnalysis(any(DocumentPiiAnalysisParams.class)))
        .thenReturn(expectedResult);

    var results = wordAnalyser.startComprehendAnalysis(PARAMS);

    assertThat(results).isEqualTo(expectedResult);

    verify(conversionService).wordToPdf(wordInput, DocumentUtils.WORD_DOCX);
    verify(scanningFileStore)
        .uploadStream(contains(S3_KEY), eq(pdfInputStream), eq((long) pdfSize));
    verify(textractAnalyser).startComprehendAnalysis(paramsCaptor.capture());

    var pdfParams = paramsCaptor.getValue();
    assertThat(pdfParams.getBucketName()).isEqualTo(SCANNING_BUCKET);
    assertThat(pdfParams.getS3Key()).contains(S3_KEY);
    assertThat(pdfParams.getDocumentId()).isEqualTo(DOCUMENT_ID);
  }
}
