package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.service.document.DocumentFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentPiiAnalysisService {

  private final DocumentFinder documentFinder;
  private final DocumentPiiAnalyserFactory analyserFactory;
  private final DocumentPiiAnalysisRunner analysisRunner;
  private final DocumentPiiDbService documentPiiDbService;

  public boolean isDocumentScannable(DocumentPiiScanCriteria scanCriteria) {
    return analyserFactory.getAnalyser(scanCriteria).isPresent();
  }

  public void startAnalysis(UUID documentId) {
    var document = documentFinder.getDocument(documentId);
    analyserFactory
        .getAnalyser(document)
        .ifPresent(analyser -> analysisRunner.runAnalysis(document, analyser));
  }

  public Page<GroupedDocumentPiiView> getPiiResultsPage(UUID documentId, Pageable pageable) {
    return documentPiiDbService.getProbablePiiPage(documentId, pageable);
  }
}
