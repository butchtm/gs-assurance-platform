package org.goldstandard.assurance.api.service.document.pii.analysis;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.util.S3Utils;

@Slf4j
@Builder
public class DocumentPiiAnalysisRunnable implements Runnable {

  private final Document document;
  private final String bucketName;
  private final DocumentPiiAnalyser analyser;
  private final DocumentPiiDbService documentPiiDbService;

  @Override
  public void run() {
    var documentId = document.getId();
    log.info("document pii analysis task starting execution for document {}", documentId);
    var analysis = documentPiiDbService.saveInProgressAnalysis(documentId);
    try {
      var startJobResult = analyser.startComprehendAnalysis(buildAnalysisParams());
      if (!startJobResult.isTextFound()) {
        documentPiiDbService.updateToCompletedNoPiiStatus(analysis);
        return;
      }
      log.info(
          "Comprehend job for document {} started successfully with job ID {}",
          documentId,
          startJobResult.getComprehendJobId());
      documentPiiDbService.updateWithJobStartResult(documentId, startJobResult);
    } catch (Exception e) {
      log.error("Error during analysis for doc {}, error: {}", documentId, e.getMessage(), e);
      documentPiiDbService.updateToFailedStatus(analysis);
    }
  }

  private DocumentPiiAnalysisParams buildAnalysisParams() {
    return DocumentPiiAnalysisParams.builder()
        .bucketName(bucketName)
        .s3Key(S3Utils.toKey(document))
        .documentName(document.getFileName())
        .documentId(document.getId())
        .contentType(document.getContentType())
        .build();
  }
}
