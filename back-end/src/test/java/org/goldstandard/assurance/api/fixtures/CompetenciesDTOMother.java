package org.goldstandard.assurance.api.fixtures;

import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompetenciesDTOMother {

  public static CompetenciesDTO empty() {
    return CompetenciesDTO.builder()
        .certifiedProductIds(Collections.emptyList())
        .vvbAssignedIds(Collections.emptyList())
        .sustainableDevelopmentGoalIds(Collections.emptyList())
        .requestTypeIds(Collections.emptyList())
        .projectTypeIds(Collections.emptyList())
        .projectScaleIds(Collections.emptyList())
        .projectLocationIds(Collections.emptyList())
        .projectGsids(Collections.emptyList())
        .projectDeveloperIds(Collections.emptyList())
        .methodologyIds(Collections.emptyList())
        .estimatedAnnualEmissionReductionAmount(null)
        .build();
  }

  public static CompetenciesDTO preFilled() {
    return CompetenciesDTO.builder()
        .vvbAssignedIds(List.of(1))
        .projectDeveloperIds(List.of(3))
        .certifiedProductIds(List.of(1))
        .sustainableDevelopmentGoalIds(List.of(1))
        .requestTypeIds(List.of(1000))
        .projectTypeIds(List.of(0))
        .projectScaleIds(List.of(1))
        .projectLocationIds(List.of(1))
        .projectGsids(List.of("GS1"))
        .methodologyIds(List.of(1))
        .estimatedAnnualEmissionReductionAmount(1)
        .build();
  }
}
