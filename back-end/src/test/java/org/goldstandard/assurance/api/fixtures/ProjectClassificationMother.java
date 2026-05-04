package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectClassificationMother {

  public static ProjectClassification withCode(String code) {
    return ProjectClassification.builder().code(code).build();
  }
}
