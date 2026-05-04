package org.goldstandard.assurance.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.enumeration.PiiProbability;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class PiiProbabilityCalculator {
  public static PiiProbability toProbability(Float score) {
    if (score > 0.80) {
      return PiiProbability.HIGH;
    } else if (score > 0.50) {
      return PiiProbability.MEDIUM;
    }
    return PiiProbability.LOW;
  }
}
