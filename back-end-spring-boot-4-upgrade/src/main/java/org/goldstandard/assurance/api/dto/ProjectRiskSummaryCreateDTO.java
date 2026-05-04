package org.goldstandard.assurance.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@NoArgsConstructor(force = true)
public class ProjectRiskSummaryCreateDTO {
  private final Boolean risk;
  private final String summary;
  private final LocalDateTime dateApplied;
}
