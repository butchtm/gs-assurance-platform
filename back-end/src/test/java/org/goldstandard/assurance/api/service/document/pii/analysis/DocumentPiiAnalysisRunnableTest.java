package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.DocumentPiiAnalysis;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentPiiAnalysisRunnableTest {

  @InjectMocks private DocumentPiiAnalysisRunnable runnable;
  @Mock private DocumentPiiAnalyser analyser;
  @Mock private DocumentPiiDbService documentPiiDbService;
  private Document document;

  @Captor private ArgumentCaptor<DocumentPiiAnalysisParams> paramsCaptor;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();
  private static final String BUCKET_NAME = "bucket";

  @BeforeEach
  void setUp() {
    document = DocumentMother.document(DOCUMENT_ID, 3);
    runnable =
        DocumentPiiAnalysisRunnable.builder()
            .document(document)
            .bucketName(BUCKET_NAME)
            .analyser(analyser)
            .documentPiiDbService(documentPiiDbService)
            .build();
  }

  @Test
  void shouldStartComprehendJobAndSaveJobDetailsIfTextFound() {
    var analysis = mock(DocumentPiiAnalysis.class);
    AnalysisStartResult startJobResult = AnalysisStartResult.builder().textFound(true).build();

    when(documentPiiDbService.saveInProgressAnalysis(DOCUMENT_ID)).thenReturn(analysis);
    when(analyser.startComprehendAnalysis(any(DocumentPiiAnalysisParams.class)))
        .thenReturn(startJobResult);

    runnable.run();

    verify(analyser).startComprehendAnalysis(paramsCaptor.capture());
    verify(documentPiiDbService).saveInProgressAnalysis(DOCUMENT_ID);
    verify(documentPiiDbService).updateWithJobStartResult(DOCUMENT_ID, startJobResult);
    var paramsUsed = paramsCaptor.getValue();
    assertThat(paramsUsed.getBucketName()).isEqualTo(BUCKET_NAME);
    assertThat(paramsUsed.getS3Key())
        .isEqualTo(document.getContainerName() + "/" + document.getFileName());
    assertThat(paramsUsed.getDocumentName()).isEqualTo(document.getFileName());
    assertThat(paramsUsed.getDocumentId()).isEqualTo(DOCUMENT_ID);
    assertThat(paramsUsed.getContentType()).isEqualTo(document.getContentType());
  }

  @Test
  void shouldSaveNoPiiFoundStatusIfTextNotFound() {
    var analysis = mock(DocumentPiiAnalysis.class);
    AnalysisStartResult startJobResult = AnalysisStartResult.builder().textFound(false).build();

    when(documentPiiDbService.saveInProgressAnalysis(DOCUMENT_ID)).thenReturn(analysis);
    when(analyser.startComprehendAnalysis(any(DocumentPiiAnalysisParams.class)))
        .thenReturn(startJobResult);

    runnable.run();

    verify(analyser).startComprehendAnalysis(paramsCaptor.capture());
    verify(documentPiiDbService).saveInProgressAnalysis(DOCUMENT_ID);
    verify(documentPiiDbService).updateToCompletedNoPiiStatus(analysis);
    var paramsUsed = paramsCaptor.getValue();
    assertThat(paramsUsed.getBucketName()).isEqualTo(BUCKET_NAME);
    assertThat(paramsUsed.getS3Key())
        .isEqualTo(document.getContainerName() + "/" + document.getFileName());
    assertThat(paramsUsed.getDocumentName()).isEqualTo(document.getFileName());
    assertThat(paramsUsed.getDocumentId()).isEqualTo(DOCUMENT_ID);
    assertThat(paramsUsed.getContentType()).isEqualTo(document.getContentType());
  }

  @Test
  void shouldSaveFailedAnalysisIfExceptionIsThrownByAnalyser() {
    var analysis = mock(DocumentPiiAnalysis.class);
    when(documentPiiDbService.saveInProgressAnalysis(DOCUMENT_ID)).thenReturn(analysis);
    when(analyser.startComprehendAnalysis(any(DocumentPiiAnalysisParams.class)))
        .thenThrow(new DocumentPiiScanException(""));

    runnable.run();

    verify(analyser).startComprehendAnalysis(paramsCaptor.capture());
    verify(documentPiiDbService).updateToFailedStatus(analysis);
    var paramsUsed = paramsCaptor.getValue();
    assertThat(paramsUsed.getBucketName()).isEqualTo(BUCKET_NAME);
    assertThat(paramsUsed.getS3Key())
        .isEqualTo(document.getContainerName() + "/" + document.getFileName());
    assertThat(paramsUsed.getDocumentName()).isEqualTo(document.getFileName());
    assertThat(paramsUsed.getDocumentId()).isEqualTo(DOCUMENT_ID);
    assertThat(paramsUsed.getContentType()).isEqualTo(document.getContentType());
  }
}
