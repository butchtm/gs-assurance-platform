package org.goldstandard.assurance.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
public class DesignCertDateMatrix {
  @Id private final Integer id;
  private final Integer requestTypeId;
  private final boolean vvbAssigned;
  private final Integer days;
}
