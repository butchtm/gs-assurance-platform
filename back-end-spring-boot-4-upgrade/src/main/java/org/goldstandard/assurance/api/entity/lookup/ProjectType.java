package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
public class ProjectType extends ReferenceDataType {

  public ProjectType() {}

  public ProjectType(Integer id) {
    this.id = id;
  }
}
