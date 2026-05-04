package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.exception.DocumentPiiScanException;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.goldstandard.assurance.api.service.document.DocumentStoreService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlainTextDocumentPiiAnalyser implements DocumentPiiAnalyser {

  private final ComprehendService comprehendService;
  private final DocumentStoreService documentStoreService;
  private final PlainTextDocumentChunker textChunker;

  private static final Long PLAIN_TEXT_SIZE_LIMIT_MB = 50L;
  private static final String TEXT_PLAIN = MediaType.TEXT_PLAIN_VALUE;

  @Override
  public Collection<ProbablePii> analyse(DocumentPiiAnalysisParams params) {
    log.info("Starting PII analysis for plain text document with S3 key: {}", params.getS3Key());

    try (var inputStream = documentStoreService.retrieveDocumentAsStream(params.getS3Key())) {
      var textContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      if (textContent.trim().isEmpty()) {
        log.info("Document {} contains no text content", params.getS3Key());
        return List.of();
      }

      var extractedTextList = textChunker.chunkTextIntoPages(textContent);
      return comprehendService.detectDocumentEntities(extractedTextList);

    } catch (IOException e) {
      log.error("Failed to read plain text document from S3: {}", params.getS3Key(), e);
      throw new DocumentPiiScanException("Failed to read plain text document");
    }
  }

  @Override
  public boolean supported(DocumentPiiScanCriteria criteria) {
    return TEXT_PLAIN.equals(criteria.getContentType())
        && criteria.getFileSizeMb() <= PLAIN_TEXT_SIZE_LIMIT_MB;
  }

  @Override
  public AnalysisStartResult startComprehendAnalysis(DocumentPiiAnalysisParams params) {
    log.error("startComprehendAnalysis method not supported for PlainTextDocumentPiiAnalyser");
    return null;
  }

  @Override
  public boolean supportsAsyncComprehend() {
    return false;
  }
}
