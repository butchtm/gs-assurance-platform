package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class CurrentStandardVersion extends ReferenceDataType {

  public CurrentStandardVersion() {}

  public CurrentStandardVersion(Integer id) {
    this.id = id;
  }
}
