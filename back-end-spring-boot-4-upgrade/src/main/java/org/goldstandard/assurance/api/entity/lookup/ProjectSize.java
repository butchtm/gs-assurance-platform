package org.goldstandard.assurance.api.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import java.util.Objects;

@Entity
public class ProjectSize extends ReferenceDataType {

  public ProjectSize() {}

  public ProjectSize(Integer id) {
    this.id = id;
  }

  @JsonIgnore
  public boolean isMicroscale() {
    return Objects.equals(code, "MICROSCALE");
  }
}
