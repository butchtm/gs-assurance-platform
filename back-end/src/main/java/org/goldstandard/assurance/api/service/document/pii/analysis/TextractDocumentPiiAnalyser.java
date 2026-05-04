package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.textract.TextractService;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextractDocumentPiiAnalyser implements DocumentPiiAnalyser {

  private final TextractService textractService;
  private final ExtractedTextS3Writer s3Writer;
  private final ComprehendService comprehendService;
  private final DocumentPiiDbService documentPiiDbService;

  private static final Long TEXTRACT_PDF_TIFF_SIZE_LIMIT = 500L;
  private static final Long TEXTRACT_JPEG_PNG_SIZE_LIMIT = 10L;

  @Override
  public AnalysisStartResult startComprehendAnalysis(DocumentPiiAnalysisParams params) {
    var textractJobId = textractService.doDetectTextJob(params.getBucketName(), params.getS3Key());
    documentPiiDbService.markTextractAsFinished(params.getDocumentId());
    var s3InputFolder = String.format("%s-%s", params.getDocumentName(), UUID.randomUUID());
    var textFound = new AtomicBoolean(false);
    extractTextIntoS3Folder(textractJobId, textFound, s3InputFolder);

    if (!textFound.get()) {
      return AnalysisStartResult.builder().textFound(false).build();
    }
    return comprehendService.detectDocumentEntitiesAsync(s3InputFolder);
  }

  private void extractTextIntoS3Folder(
      String textractJobId, AtomicBoolean textFound, String s3InputFolder) {
    textractService
        .extractWords(textractJobId)
        .filter(text -> !text.isEmpty())
        .forEach(
            text -> {
              textFound.set(true);
              s3Writer.writePagesToS3(text, s3InputFolder);
            });
  }

  @Override
  public boolean supported(DocumentPiiScanCriteria criteria) {
    return isSupportedPdfOrTiff(criteria) || isSupportedJpegOrPng(criteria);
  }

  private boolean isSupportedPdfOrTiff(DocumentPiiScanCriteria criteria) {
    return criteria.getFileSizeMb() <= TEXTRACT_PDF_TIFF_SIZE_LIMIT
        && DocumentUtils.isPdfOrTiff(criteria.getContentType());
  }

  private boolean isSupportedJpegOrPng(DocumentPiiScanCriteria criteria) {
    return criteria.getFileSizeMb() <= TEXTRACT_JPEG_PNG_SIZE_LIMIT
        && DocumentUtils.isJpegOrPng(criteria.getContentType());
  }

  @Override
  public Collection<ProbablePii> analyse(DocumentPiiAnalysisParams params) {
    log.error("Synchronous analyse method not supported for TextractDocumentPiiAnalyser");
    return Collections.emptyList();
  }

  @Override
  public boolean supportsAsyncComprehend() {
    return true;
  }
}
