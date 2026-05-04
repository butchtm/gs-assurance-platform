package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;

@Entity
public class InvoiceStatus extends ReferenceDataType {
  public static final Integer NOT_TRIGGERED_ID = 0;
  public static final Integer GENERATED_ID = 2;

  public InvoiceStatus() {}

  public InvoiceStatus(Integer id) {
    this.id = id;
  }

  public static InvoiceStatus generated() {
    return new InvoiceStatus(GENERATED_ID);
  }

  public static InvoiceStatus notTriggered() {
    return new InvoiceStatus(NOT_TRIGGERED_ID);
  }
}
