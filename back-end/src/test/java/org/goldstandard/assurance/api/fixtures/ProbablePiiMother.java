package org.goldstandard.assurance.api.fixtures;

import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.ProbablePii;
import org.goldstandard.assurance.api.enumeration.PiiProbability;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProbablePiiMother {

  public static Collection<ProbablePii> buildList() {
    return List.of(ProbablePii.builder().probability(PiiProbability.MEDIUM).pageNumber(1).build());
  }
}
