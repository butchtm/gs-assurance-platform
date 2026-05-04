package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import tools.jackson.databind.JsonNode;

@Data
@Builder
@RequiredArgsConstructor
public class RequestFormDTO {
  private final Integer id;
  private final Double version;
  private final ReferenceDataType type;
  private final JsonNode schema;
  private final JsonNode data;
  private final ReferenceDataType status;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
  private final ZonedDateTime submittedAt;
}
