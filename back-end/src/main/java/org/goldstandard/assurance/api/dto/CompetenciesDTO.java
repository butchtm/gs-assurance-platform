package org.goldstandard.assurance.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.annotations.CheckAtLeastOneNotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CheckAtLeastOneNotNull
public class CompetenciesDTO {
  private Integer accountId;
  private Integer estimatedAnnualEmissionReductionAmount;
  private List<String> projectGsids;
  private List<Integer> projectTypeIds;
  private List<Integer> projectLocationIds;
  private List<Integer> projectScaleIds;
  private List<Integer> methodologyIds;
  private List<Integer> requestTypeIds;
  private List<Integer> certifiedProductIds;
  private List<Integer> vvbAssignedIds;
  private List<Integer> projectDeveloperIds;
  private List<Integer> sustainableDevelopmentGoalIds;
}
