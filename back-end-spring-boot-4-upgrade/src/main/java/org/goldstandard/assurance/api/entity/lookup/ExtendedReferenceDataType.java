package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedReferenceDataType extends ReferenceDataType {

  private Boolean isActive;

  public boolean isActive() {
    return Boolean.TRUE.equals(isActive);
  }

  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }
}
