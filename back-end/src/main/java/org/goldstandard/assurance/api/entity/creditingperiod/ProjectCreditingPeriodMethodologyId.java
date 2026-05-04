package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@Builder
@Embeddable
@Getter
@ToString
public class ProjectCreditingPeriodMethodologyId implements Serializable {
  private final Integer projectId;
  private final Integer periodId;
  private final Integer methodologyId;
}
