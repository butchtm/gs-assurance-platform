package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Embeddable
public class ExternalSystemProjectUpdateId implements Serializable {
  private final Integer projectId;
  private final String destinationSystem;
}
