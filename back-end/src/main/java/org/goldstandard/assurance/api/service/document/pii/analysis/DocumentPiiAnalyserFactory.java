package org.goldstandard.assurance.api.service.document.pii.analysis;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentPiiAnalyserFactory {

  private final List<DocumentPiiAnalyser> analysers;

  @Value("${document.piiScanningEnabled:true}")
  private boolean documentPiiScanningEnabled;

  public Optional<DocumentPiiAnalyser> getAnalyser(Document document) {
    return getAnalyser(toParams(document));
  }

  public Optional<DocumentPiiAnalyser> getAnalyser(DocumentPiiScanCriteria criteria) {
    if (!documentPiiScanningEnabled) {
      return Optional.empty();
    }
    return analysers.stream().filter(analyser -> analyser.supported(criteria)).findFirst();
  }

  private DocumentPiiScanCriteria toParams(Document document) {
    return DocumentPiiScanCriteria.builder()
        .contentType(document.getContentType())
        .fileSizeMb(DocumentUtils.getFileSizeMb(document))
        .build();
  }
}
