package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.ProjectClassificationDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.springframework.stereotype.Component;

@Component
public class ProjectClassificationConverter {

  public ProjectClassificationDTO toDTO(ProjectClassification projectClassification) {
    return ProjectClassificationDTO.builder()
        .id(projectClassification.getId())
        .code(projectClassification.getCode())
        .description(projectClassification.getDescription())
        .active(projectClassification.getActive())
        .build();
  }
}
