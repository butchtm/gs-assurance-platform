package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class ReferenceDataViewId implements Serializable {
  private String enumeration;
  private Integer id;
}
