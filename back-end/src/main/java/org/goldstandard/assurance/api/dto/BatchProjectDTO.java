package org.goldstandard.assurance.api.dto;

import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.Methodology;

@Getter
@Setter
@Builder
public class BatchProjectDTO {
  private final Integer id;
  private final String name;
  private final String gsid;
  private final String classification;
  private final Integer requestId;

  @With(AccessLevel.PRIVATE)
  private final Collection<ProjectCreditingPeriod> creditingPeriods;

  private final Collection<Methodology> methodologies;

  private final ProjectRiskSummaryDTO currentRiskSummary;

  public BatchProjectDTO clearCreditingPeriods() {
    return withCreditingPeriods(Collections.emptyList());
  }
}
