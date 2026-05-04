package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentPiiAnalysisResultsFactory {

  private final RequestFinder requestFinder;

  public DocumentPiiAnalysisResults build(Document document, Collection<ProbablePii> pii) {
    var request = requestFinder.getRequestAssociatedToDocument(document.getId());

    return DocumentPiiAnalysisResults.builder()
        .document(document)
        .request(request)
        .project(request.getProject())
        .probablePii(pii)
        .build();
  }
}
