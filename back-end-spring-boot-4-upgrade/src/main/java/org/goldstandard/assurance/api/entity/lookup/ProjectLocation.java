package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
@Entity
public class ProjectLocation extends ReferenceDataType {

  private boolean leastDevelopedCountry;

  public ProjectLocation() {}

  public ProjectLocation(Integer id) {
    this.id = id;
  }
}
