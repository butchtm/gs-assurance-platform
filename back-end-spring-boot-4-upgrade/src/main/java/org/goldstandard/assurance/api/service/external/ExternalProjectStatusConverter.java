package org.goldstandard.assurance.api.service.external;

import org.springframework.stereotype.Component;

@Component
public class ExternalProjectStatusConverter {

  public String toExternalProjectStatus(String code) {
    return switch (code) {
      case "NEW" -> "NEW_PROJECT";
      case "DESIGN_CERTIFIED" -> "GOLD_STANDARD_CERTIFIED_DESIGN";
      case "CERTIFIED_PROJECT" -> "GOLD_STANDARD_CERTIFIED_PROJECT";
      case "DE_CERTIFIED" -> "DE_REGISTERED";
      default -> code;
    };
  }
}
