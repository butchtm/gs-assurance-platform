package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class RegistrationType extends ReferenceDataType {

  public RegistrationType() {}

  public RegistrationType(Integer id) {
    this.id = id;
  }
}
