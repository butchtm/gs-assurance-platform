package org.goldstandard.assurance.api.entity.lookup;

import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class TransitionFromOtherScheme extends ReferenceDataType {

  public TransitionFromOtherScheme(Integer id) {
    this.id = id;
  }

  public boolean isNone() {
    return Objects.equals(id, 0);
  }
}
