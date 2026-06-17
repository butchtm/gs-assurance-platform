package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.ProjectClassificationDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassificationSemantics;
import org.springframework.stereotype.Component;

@Component
public class ProjectClassificationConverter {

  public ProjectClassificationDTO toDTO(ProjectClassification projectClassification) {
    var displayClassification =
        ProjectClassificationSemantics.toDisplayClassification(projectClassification);
    return ProjectClassificationDTO.builder()
        .id(displayClassification.getId())
        .code(displayClassification.getCode())
        .description(displayClassification.getDescription())
        .active(displayClassification.getActive())
        .build();
  }
}
