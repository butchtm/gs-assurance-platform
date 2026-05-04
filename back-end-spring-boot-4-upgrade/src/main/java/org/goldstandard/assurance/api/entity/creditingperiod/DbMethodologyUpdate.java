package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Entity
@IdClass(DbMethodologyUpdateId.class)
@Table(name = "methodology_update", schema = "gs")
public class DbMethodologyUpdate {
  @Id private final Integer projectMethodologyUpdateId;
  @Id private final Integer methodologyId;
  private final Double initialMethodologyVersion;
  private final Double newMethodologyVersion;

  @Enumerated(EnumType.STRING)
  private final MethodologyOperation operation;
}
