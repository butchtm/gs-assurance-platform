package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
public class FeeSchedule extends ReferenceDataType {

  public FeeSchedule() {}

  public FeeSchedule(Integer id) {
    this.id = id;
  }
}
