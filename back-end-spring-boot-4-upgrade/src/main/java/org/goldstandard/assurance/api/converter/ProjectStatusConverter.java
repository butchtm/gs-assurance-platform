package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.ProjectStatusDTO;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectStatusConverter {

  public ProjectStatusDTO toDTO(ProjectStatus projectStatus) {
    return ProjectStatusDTO.builder()
        .id(projectStatus.getId())
        .code(projectStatus.getCode())
        .description(projectStatus.getDescription())
        .build();
  }
}
