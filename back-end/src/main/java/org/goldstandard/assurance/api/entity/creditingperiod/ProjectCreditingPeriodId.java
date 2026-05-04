package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Data
@Embeddable
public class ProjectCreditingPeriodId implements Serializable {
  @Column(name = "project_id")
  private final Integer projectId;

  @Column(name = "period_id")
  private final Integer periodId;
}
