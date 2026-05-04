package org.goldstandard.assurance.api.service.pii;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.service.comprehend.ComprehendService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PiiAnalysisService {
  private final ComprehendService comprehendService;

  public Collection<ProbablePii> analyse(TextAnalysisDTO textAnalysis) {
    return comprehendService.detectEntities(textAnalysis.getFieldsToAnalyse());
  }
}
