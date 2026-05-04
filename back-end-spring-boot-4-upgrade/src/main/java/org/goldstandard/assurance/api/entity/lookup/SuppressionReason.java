package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class SuppressionReason extends ExtendedReferenceDataType {

  public SuppressionReason() {}

  public SuppressionReason(Integer id) {
    this.id = id;
  }
}
