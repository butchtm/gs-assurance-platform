package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class BatchType extends ReferenceDataType {

  private static final int LEGACY_VPA_CPA_BATCH_TYPE_ID = 4;

  public BatchType() {}

  public BatchType(Integer id) {
    this.id = id;
  }

  public boolean isTwoOrMoreVpaCpa() {
    return id == LEGACY_VPA_CPA_BATCH_TYPE_ID;
  }
}
