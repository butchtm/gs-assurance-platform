package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.validator.JsonNotEmpty;
import tools.jackson.databind.JsonNode;

@Data
@Builder
@RequiredArgsConstructor
public class FormDefinitionUpdateDTO {
  @JsonNotEmpty private final JsonNode schema;
}
