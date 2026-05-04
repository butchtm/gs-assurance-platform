package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.entity.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentPiiResultsProcessor {

  private final DocumentPiiDbService documentPiiDbService;
  private final DocumentPiiAnalysisResultsFactory resultsFactory;

  public void processResults(Collection<ProbablePii> probablePii, Document document) {
    if (!probablePii.isEmpty()) {
      log.info(
          "PII scanning found {} PII entities for document {}",
          probablePii.size(),
          document.getId());
      var results = resultsFactory.build(document, probablePii);
      documentPiiDbService.saveAnalysisResults(results);
    } else {
      log.info("PII scanning found no PII for document {}", document.getId());
      documentPiiDbService.updateToCompletedNoPiiStatus(document.getId());
    }
  }
}
