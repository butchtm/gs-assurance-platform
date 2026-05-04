package org.goldstandard.assurance.api.fixtures;

import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCompetenciesEntityMother {

  public static Competencies createEmptyCompetencies() {
    return Competencies.builder()
        .certifiedProducts(Collections.emptyList())
        .vvbsAssigned(Collections.emptyList())
        .sustainableDevelopmentGoals(Collections.emptyList())
        .requestTypes(Collections.emptyList())
        .projectTypes(Collections.emptyList())
        .projectLocations(Collections.emptyList())
        .projectScales(Collections.emptyList())
        .projectLocations(Collections.emptyList())
        .projectGsids(Collections.emptyList())
        .projectDevelopers(Collections.emptyList())
        .methodologies(Collections.emptyList())
        .estimatedAnnualEmissionReductionAmount(null)
        .build();
  }

  public static Competencies createPreFilledCompetencies() {
    return Competencies.builder()
        .certifiedProducts(List.of(new ProjectAsset(1)))
        .vvbsAssigned(List.of(new Account(1)))
        .sustainableDevelopmentGoals(List.of(new SustainableDevelopmentGoal(1)))
        .requestTypes(List.of(new RequestType(1)))
        .projectTypes(List.of(new ProjectType(1)))
        .projectLocations(List.of(new ProjectLocation(1)))
        .projectScales(List.of(new ProjectSize(1)))
        .projectLocations(List.of(new ProjectLocation(1)))
        .projectGsids(List.of("GS1"))
        .projectDevelopers(List.of(new Account(1)))
        .methodologies(List.of(new Methodology(1)))
        .estimatedAnnualEmissionReductionAmount(1)
        .build();
  }
}
