package org.goldstandard.assurance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.enumeration.PiiProbability;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ProbablePii {
  private final String entity;
  private final PiiProbability probability;
  private final String identifiedString;
  private final String field;
  private final Integer pageNumber;
  private final String location;
}
