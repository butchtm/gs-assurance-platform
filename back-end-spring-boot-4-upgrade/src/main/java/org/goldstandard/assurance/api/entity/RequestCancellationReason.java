package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@NoArgsConstructor
public class RequestCancellationReason extends ReferenceDataType {
  private static final int OTHER_REASON_ID = 8;

  public RequestCancellationReason(Integer id) {
    this.id = id;
  }

  public boolean isOtherReason() {
    return this.id.equals(OTHER_REASON_ID);
  }
}
