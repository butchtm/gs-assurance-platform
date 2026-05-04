package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class RequestRoundStatus extends ReferenceDataType {
  public static final Integer OPEN = 1;
  public static final Integer CLOSED = 2;

  public RequestRoundStatus() {}

  public RequestRoundStatus(Integer id) {
    this.id = id;
  }
}
