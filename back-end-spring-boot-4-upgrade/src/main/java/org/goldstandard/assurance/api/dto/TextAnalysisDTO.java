package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TextAnalysisDTO {
  private final Collection<FieldTextAnalysisDTO> fieldsToAnalyse;
}
