package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Entity
@NoArgsConstructor
public class DigitalFormType extends ReferenceDataType {

  public DigitalFormType(Integer id) {
    this.id = id;
  }

  public static DigitalFormType annualReport() {
    return new DigitalFormType(0);
  }
}
