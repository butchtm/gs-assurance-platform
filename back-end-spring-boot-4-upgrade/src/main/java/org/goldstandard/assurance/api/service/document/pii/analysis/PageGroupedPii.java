package org.goldstandard.assurance.api.service.document.pii.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.enumeration.PiiProbability;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class PageGroupedPii {
  private final String entity;
  private final PiiProbability probability;
  private final Integer pageNumber;
  private final String location;
  private final int occurrences;
}
