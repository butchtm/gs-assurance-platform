package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DocumentPiiAnalysisRunner {

  @Value("${s3.bucket-name.document}")
  private String bucketName;

  private final DocumentPiiDbService documentPiiDbService;
  private final DocumentPiiResultsProcessor resultsProcessor;
  private final ThreadPoolTaskExecutor taskExecutor;

  public void runAnalysis(Document document, DocumentPiiAnalyser analyser) {
    log.info("running pii analysis for document with ID {}", document.getId());
    var task = buildRunnable(document, analyser);
    try {
      taskExecutor.execute(task);
      log.info("run pii analysis task execution triggered for document {}", document.getId());
    } catch (RejectedExecutionException e) {
      log.error(
          "pii analysis task rejected for document {}, error: {}",
          document.getId(),
          e.getMessage(),
          e);
    }
  }

  private Runnable buildRunnable(Document document, DocumentPiiAnalyser analyser) {
    return analyser.supportsAsyncComprehend()
        ? buildAsyncRunnable(document, analyser)
        : buildSyncRunnable(document, analyser);
  }

  private DocumentPiiAnalysisRunnable buildAsyncRunnable(
      Document document, DocumentPiiAnalyser analyser) {
    return DocumentPiiAnalysisRunnable.builder()
        .document(document)
        .bucketName(bucketName)
        .analyser(analyser)
        .documentPiiDbService(documentPiiDbService)
        .build();
  }

  private DocumentPiiAnalysisSynchronousRunnable buildSyncRunnable(
      Document document, DocumentPiiAnalyser analyser) {
    return DocumentPiiAnalysisSynchronousRunnable.builder()
        .document(document)
        .bucketName(bucketName)
        .analyser(analyser)
        .documentPiiDbService(documentPiiDbService)
        .resultsProcessor(resultsProcessor)
        .build();
  }
}
