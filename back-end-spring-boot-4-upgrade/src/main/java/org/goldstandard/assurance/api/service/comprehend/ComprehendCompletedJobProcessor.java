package org.goldstandard.assurance.api.service.comprehend;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.document.DocumentService;
import org.goldstandard.assurance.api.service.document.pii.analysis.ComprehendResultsParserFactory;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiResultsProcessor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.comprehend.model.PiiEntitiesDetectionJobProperties;

@Component
@RequiredArgsConstructor
public class ComprehendCompletedJobProcessor {

  private final DocumentService documentService;
  private final ComprehendResultsRetriever resultsRetriever;
  private final ComprehendResultsParserFactory parserFactory;
  private final DocumentPiiResultsProcessor resultsProcessor;

  public void processJobOutput(UUID documentId, PiiEntitiesDetectionJobProperties detectPiiJob) {
    var document = documentService.forceGetDocument(documentId);
    var comprehendOutput = resultsRetriever.retrieve(detectPiiJob.outputDataConfig());
    var probablePii = parserFactory.getParser(document.getContentType()).parse(comprehendOutput);
    resultsProcessor.processResults(probablePii, document);
  }
}
