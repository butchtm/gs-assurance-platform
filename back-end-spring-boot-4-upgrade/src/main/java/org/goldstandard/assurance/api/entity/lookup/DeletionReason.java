package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class DeletionReason extends ExtendedReferenceDataType {

  public DeletionReason() {}

  public DeletionReason(Integer id) {
    this.id = id;
  }
}
