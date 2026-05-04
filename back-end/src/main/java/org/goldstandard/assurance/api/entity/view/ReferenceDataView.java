package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import net.jcip.annotations.Immutable;

@Immutable
@Entity(name = "v_reference_enumeration")
public class ReferenceDataView {

  // The view doesn't have a primary key we have to create a composite one here for spring data JPA.
  @EmbeddedId private ReferenceDataViewId id;
  private String code;
  private String description;
  private boolean isActive;

  public ReferenceDataViewId getId() {
    return id;
  }

  public void setId(ReferenceDataViewId id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
