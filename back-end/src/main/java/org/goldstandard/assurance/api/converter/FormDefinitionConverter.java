package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.FormDefinitionDTO;
import org.goldstandard.assurance.api.entity.FormDefinition;
import org.springframework.stereotype.Component;

@Component
public class FormDefinitionConverter {

  public FormDefinitionDTO toDTO(FormDefinition definition) {
    return FormDefinitionDTO.builder()
        .id(definition.getId())
        .version(definition.getVersion())
        .status(definition.getStatus())
        .type(definition.getType())
        .schema(definition.getSchema())
        .createdAt(definition.getCreatedAt())
        .updatedAt(definition.getUpdatedAt())
        .publishedAt(definition.getPublishedAt())
        .build();
  }
}
