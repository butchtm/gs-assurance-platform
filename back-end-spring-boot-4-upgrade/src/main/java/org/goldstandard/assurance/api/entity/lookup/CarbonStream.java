package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class CarbonStream extends ReferenceDataType {

  public CarbonStream() {}

  public CarbonStream(Integer id) {
    this.id = id;
  }
}
