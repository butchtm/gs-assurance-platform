package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.S3ConfigurationProperties;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.service.document.FileStore;
import org.goldstandard.assurance.api.service.document.conversion.DocumentConversionService;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WordDocumentPiiAnalyser implements DocumentPiiAnalyser {

  private final FileStore documentFileStore;
  private final DocumentConversionService conversionService;
  private final FileStore scanningFileStore;
  private final S3ConfigurationProperties s3Properties;
  private final TextractDocumentPiiAnalyser textractAnalyser;

  public WordDocumentPiiAnalyser(
      @Qualifier("documentFileStore") FileStore documentFileStore,
      DocumentConversionService conversionService,
      @Qualifier("piiScanningFileStore") FileStore scanningFileStore,
      S3ConfigurationProperties s3Properties,
      TextractDocumentPiiAnalyser textractAnalyser) {
    this.documentFileStore = documentFileStore;
    this.conversionService = conversionService;
    this.scanningFileStore = scanningFileStore;
    this.s3Properties = s3Properties;
    this.textractAnalyser = textractAnalyser;
  }

  @Override
  public AnalysisStartResult startComprehendAnalysis(DocumentPiiAnalysisParams params) {
    var pdfS3Key = params.getS3Key() + "-" + UUID.randomUUID();
    uploadWordDocumentConvertedToPdf(params, pdfS3Key);
    var pdfParams =
        DocumentPiiAnalysisParams.builder()
            .documentId(params.getDocumentId())
            .bucketName(s3Properties.getPiiScanningBucketName())
            .s3Key(pdfS3Key)
            .build();
    return textractAnalyser.startComprehendAnalysis(pdfParams);
  }

  private void uploadWordDocumentConvertedToPdf(DocumentPiiAnalysisParams params, String pdfS3Key) {
    try (var wordDocStream = documentFileStore.getObjectAsStream(params.getS3Key())) {
      var pdfStream = conversionService.wordToPdf(wordDocStream, params.getContentType());
      scanningFileStore.uploadStream(pdfS3Key, pdfStream.toInputStream(), (long) pdfStream.size());
    } catch (OfficeException | IOException e) {
      throw new DocumentPiiScanException(
          "Error converting Word document to PDF for PII scan: " + e.getMessage());
    }
  }

  @Override
  public boolean supported(DocumentPiiScanCriteria criteria) {
    return DocumentUtils.isWordDoc(criteria.getContentType());
  }

  @Override
  public Collection<ProbablePii> analyse(DocumentPiiAnalysisParams params) {
    log.error("Synchronous analyse method not supported for WordDocumentPiiAnalyser");
    return Collections.emptyList();
  }

  @Override
  public boolean supportsAsyncComprehend() {
    return true;
  }
}
