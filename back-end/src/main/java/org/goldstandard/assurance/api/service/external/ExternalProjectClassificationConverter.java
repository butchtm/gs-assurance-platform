package org.goldstandard.assurance.api.service.external;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassificationSemantics;

@RequiredArgsConstructor
public class ExternalProjectClassificationConverter {

  private final String noneCode;

  public String toExternalClassification(String code) {
    return switch (ProjectClassificationSemantics.toDisplayCode(code)) {
      case "NONE", "STANDALONE_PROJECT" -> noneCode;
      case "POA", "PROGRAMME_OF_ACTIVITIES", "REAL_OR_REGULAR_CASE_POA" -> "POA";
      case "REAL_CASE_VPA", "REAL_CASE_VPA_LEGACY", "REGULAR_CASE_VPA", "REGULAR_VPA", "VPA_CPA" ->
          "VPA";
      default -> code;
    };
  }
}
