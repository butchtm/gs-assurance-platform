package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class FormDefinitionPublishDTO {
  private final JsonNode schema;
  private final boolean increaseMajorVersion;
}
