package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class RequestDocumentId implements Serializable {
  private Integer requestId;
  private UUID documentId;
}
