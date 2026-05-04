package org.goldstandard.assurance.api.service.external.registry;

import org.goldstandard.assurance.api.entity.Project;
import org.springframework.util.CollectionUtils;

public class RegistryProjectMethodologyConverter {

  public String toMethodologyDescription(Project project) {
    var methodologies = project.getMethodologies();
    if (CollectionUtils.isEmpty(methodologies)) {
      return null;
    }
    return methodologies.getFirst().getDescription();
  }
}
