package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class SustainableDevelopmentGoal extends ReferenceDataType {

  public SustainableDevelopmentGoal() {}

  public SustainableDevelopmentGoal(Integer id) {
    this.id = id;
  }
}
