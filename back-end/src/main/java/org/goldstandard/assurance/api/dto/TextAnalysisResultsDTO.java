package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TextAnalysisResultsDTO {
  private final Collection<ProbablePii> probablePii;

  public Collection<String> getDistinctFields() {
    return probablePii.stream().map(ProbablePii::getField).collect(Collectors.toSet());
  }
}
