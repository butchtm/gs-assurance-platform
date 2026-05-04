package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class RequestTypeDocumentTypeId implements Serializable {
  private Integer requestTypeId;
  private Integer documentTypeId;
}
