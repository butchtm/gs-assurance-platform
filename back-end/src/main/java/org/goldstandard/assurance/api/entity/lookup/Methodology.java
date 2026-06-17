package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
@Entity
@AllArgsConstructor
public class Methodology extends ExtendedReferenceDataType {
  private boolean paa;

  public Methodology() {}

  public Methodology(Integer id) {
    this.id = id;
    this.paa = false;
  }
}
