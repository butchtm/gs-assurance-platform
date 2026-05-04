package org.goldstandard.assurance.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Data
@Builder
@RequiredArgsConstructor
public class FormDefinitionDTO {
  private final Integer id;
  private final Double version;
  private final ReferenceDataType type;
  private final JsonNode schema;
  private final ReferenceDataType status;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
  private final ZonedDateTime publishedAt;
}
