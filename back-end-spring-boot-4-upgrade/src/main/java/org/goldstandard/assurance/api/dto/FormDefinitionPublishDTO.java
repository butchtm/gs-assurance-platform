package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@Builder
@RequiredArgsConstructor
public class FormDefinitionPublishDTO {
  private final JsonNode schema;
  private final boolean increaseMajorVersion;
}
