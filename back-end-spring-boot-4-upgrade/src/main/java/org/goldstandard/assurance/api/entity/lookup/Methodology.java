package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Entity
@AllArgsConstructor
public class Methodology extends ExtendedReferenceDataType {

  public Methodology(Integer id) {
    this.id = id;
  }
}
