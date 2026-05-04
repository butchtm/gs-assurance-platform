package org.goldstandard.assurance.api.converter;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentPiiScanCriteriaDTO;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.entity.view.GroupedDocumentPiiView;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiScanCriteria;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;
import org.goldstandard.assurance.api.util.DocumentUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PiiAnalysisConverter {

  public TextAnalysisResultsDTO toDTO(Collection<ProbablePii> probablePii) {
    return TextAnalysisResultsDTO.builder().probablePii(probablePii).build();
  }

  public DocumentPiiScanCriteria toAnalysisCriteria(DocumentPiiScanCriteriaDTO dto) {
    return DocumentPiiScanCriteria.builder()
        .contentType(dto.getContentType())
        .fileSizeMb(DocumentUtils.toMegaBytes(dto.getDocumentSizeBytes()))
        .build();
  }

  public PageGroupedPii toPageGroupedPii(GroupedDocumentPiiView documentPii) {
    return PageGroupedPii.builder()
        .entity(documentPii.getEntity())
        .probability(documentPii.getProbability())
        .pageNumber(documentPii.getPageNumber())
        .location(documentPii.getLocation())
        .occurrences(documentPii.getCount())
        .build();
  }
}
