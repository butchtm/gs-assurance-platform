package org.goldstandard.assurance.api.service.finance;

import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;

@Slf4j
public class FinanceProjectClassificationFactory {

  public String toFinanceProjectClassificationCode(ProjectClassification classification) {
    var code = classification.getCode();
    return switch (code) {
      case "REGULAR_CASE_VPA", "REGULAR_VPA" -> "REGULAR_VPA";
      case "REAL_CASE_VPA_LEGACY", "REAL_CASE_VPA" -> "REAL_CASE_VPA";
      case "POA", "REAL_OR_REGULAR_CASE_POA", "PROGRAMME_OF_ACTIVITIES" ->
          "PROGRAMME_OF_ACTIVITIES";
      default -> code;
    };
  }
}
