package org.goldstandard.assurance.api.service.document.pii.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@ExtendWith(MockitoExtension.class)
class DocumentPiiAnalysisRunnerTest {

  @InjectMocks private DocumentPiiAnalysisRunner runner;
  @Mock private DocumentPiiDbService documentPiiDbService;
  @Mock private DocumentPiiResultsProcessor resultsProcessor;
  @Mock private ThreadPoolTaskExecutor taskExecutor;
  @Mock private TextractDocumentPiiAnalyser textractAnalyser;
  @Mock private PlainTextDocumentPiiAnalyser plainTextAnalyser;

  @Captor private ArgumentCaptor<Runnable> runnableCaptor;

  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @Test
  void shouldRunAsyncAnalysisIfSupportedByAnalyser() {
    var document = DocumentMother.withContentType(DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);

    when(textractAnalyser.supportsAsyncComprehend()).thenReturn(true);

    runner.runAnalysis(document, textractAnalyser);

    verify(taskExecutor).execute(runnableCaptor.capture());
    var runnableUsed = runnableCaptor.getValue();
    assertThat(runnableUsed)
        .isInstanceOf(DocumentPiiAnalysisRunnable.class)
        .hasFieldOrPropertyWithValue("document", document)
        .hasFieldOrProperty("bucketName")
        .hasFieldOrPropertyWithValue("analyser", textractAnalyser)
        .hasFieldOrPropertyWithValue("documentPiiDbService", documentPiiDbService);
  }

  @Test
  void shouldRunSynchronousAnalysisIfAsyncNotSupportedByAnalyser() {
    var document = DocumentMother.withContentType(DOCUMENT_ID, MediaType.APPLICATION_PDF_VALUE);

    when(plainTextAnalyser.supportsAsyncComprehend()).thenReturn(false);

    runner.runAnalysis(document, plainTextAnalyser);

    verify(taskExecutor).execute(runnableCaptor.capture());
    var runnableUsed = runnableCaptor.getValue();
    assertThat(runnableUsed)
        .isInstanceOf(DocumentPiiAnalysisSynchronousRunnable.class)
        .hasFieldOrPropertyWithValue("document", document)
        .hasFieldOrProperty("bucketName")
        .hasFieldOrPropertyWithValue("analyser", plainTextAnalyser)
        .hasFieldOrPropertyWithValue("documentPiiDbService", documentPiiDbService)
        .hasFieldOrPropertyWithValue("resultsProcessor", resultsProcessor);
  }
}
