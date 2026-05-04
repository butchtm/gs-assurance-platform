package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.validator.JsonNotEmpty;

@Data
@Builder
@RequiredArgsConstructor
public class RequestFormUpdateDTO {
  @JsonNotEmpty private final JsonNode data;
}
