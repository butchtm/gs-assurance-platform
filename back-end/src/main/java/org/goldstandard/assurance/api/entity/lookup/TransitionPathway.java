package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class TransitionPathway extends ReferenceDataType {

  public TransitionPathway() {}

  public TransitionPathway(Integer id) {
    this.id = id;
  }
}
