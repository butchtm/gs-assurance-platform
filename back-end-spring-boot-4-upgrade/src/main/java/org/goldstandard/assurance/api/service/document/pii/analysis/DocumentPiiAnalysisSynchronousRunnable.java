package org.goldstandard.assurance.api.service.document.pii.analysis;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.util.S3Utils;

@Slf4j
@Builder
public class DocumentPiiAnalysisSynchronousRunnable implements Runnable {

  private final Document document;
  private final String bucketName;
  private final DocumentPiiAnalyser analyser;
  private final DocumentPiiDbService documentPiiDbService;
  private final DocumentPiiResultsProcessor resultsProcessor;

  @Override
  public void run() {
    log.info("document pii analysis task starting execution for document {}", document.getId());
    var analysis = documentPiiDbService.saveInProgressAnalysis(document.getId());
    try {
      var probablePii = analyser.analyse(buildAnalysisParams());
      log.info("PII analysis of document {} complete. Now processing results", document.getId());
      resultsProcessor.processResults(probablePii, document);
    } catch (Exception e) {
      log.error("Error during analysis for doc {}, error: {}", document.getId(), e.getMessage(), e);
      documentPiiDbService.updateToFailedStatus(analysis);
    }
  }

  private DocumentPiiAnalysisParams buildAnalysisParams() {
    return DocumentPiiAnalysisParams.builder()
        .bucketName(bucketName)
        .s3Key(S3Utils.toKey(document))
        .build();
  }
}
