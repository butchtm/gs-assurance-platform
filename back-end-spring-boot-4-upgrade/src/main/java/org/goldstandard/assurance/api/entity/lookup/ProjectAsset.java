package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class ProjectAsset extends ReferenceDataType {

  public ProjectAsset() {}

  public ProjectAsset(Integer id) {
    this.id = id;
  }
}
