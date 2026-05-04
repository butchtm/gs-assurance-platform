package org.goldstandard.assurance.api.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.springframework.stereotype.Component;

@Component
public class CompetenciesConverter {

  public void toEntity(Competencies competencies, CompetenciesDTO competenciesDto) {
    competencies.setProjectGsids(competenciesDto.getProjectGsids());
    competencies.setCertifiedProducts(
        toList(competenciesDto.getCertifiedProductIds(), ProjectAsset::new));
    competencies.setProjectLocations(
        toList(competenciesDto.getProjectLocationIds(), ProjectLocation::new));
    competencies.setMethodologies(toList(competenciesDto.getMethodologyIds(), Methodology::new));
    competencies.setProjectScales(toList(competenciesDto.getProjectScaleIds(), ProjectSize::new));
    competencies.setRequestTypes(toList(competenciesDto.getRequestTypeIds(), RequestType::new));
    competencies.setProjectTypes(toList(competenciesDto.getProjectTypeIds(), ProjectType::new));
    competencies.setProjectDevelopers(
        toList(competenciesDto.getProjectDeveloperIds(), Account::new));
    competencies.setVvbsAssigned(toList(competenciesDto.getVvbAssignedIds(), Account::new));
    competencies.setEstimatedAnnualEmissionReductionAmount(
        competenciesDto.getEstimatedAnnualEmissionReductionAmount());
    competencies.setSustainableDevelopmentGoals(
        toList(
            competenciesDto.getSustainableDevelopmentGoalIds(), SustainableDevelopmentGoal::new));
  }

  public void toDto(Competencies competencies, CompetenciesDTO competenciesDto, Integer accountId) {
    competenciesDto.setAccountId(accountId);
    competenciesDto.setProjectGsids(competencies.getProjectGsids());
    competenciesDto.setCertifiedProductIds(
        toIds(competencies.getCertifiedProducts(), ProjectAsset::getId));
    competenciesDto.setProjectLocationIds(
        toIds(competencies.getProjectLocations(), ProjectLocation::getId));
    competenciesDto.setMethodologyIds(toIds(competencies.getMethodologies(), Methodology::getId));
    competenciesDto.setProjectScaleIds(toIds(competencies.getProjectScales(), ProjectSize::getId));
    competenciesDto.setRequestTypeIds(toIds(competencies.getRequestTypes(), RequestType::getId));
    competenciesDto.setProjectTypeIds(toIds(competencies.getProjectTypes(), ProjectType::getId));
    competenciesDto.setProjectDeveloperIds(
        toIds(competencies.getProjectDevelopers(), Account::getId));
    competenciesDto.setVvbAssignedIds(toIds(competencies.getVvbsAssigned(), Account::getId));
    competenciesDto.setEstimatedAnnualEmissionReductionAmount(
        competencies.getEstimatedAnnualEmissionReductionAmount());
    competenciesDto.setSustainableDevelopmentGoalIds(
        toIds(competencies.getSustainableDevelopmentGoals(), SustainableDevelopmentGoal::getId));
  }

  /**
   * This method exists to solve an issue found with the in-memory db when running the
   * 'AccountCompetenciesApiIntegrationTest.java' update test. The in-memory db was trying to modify
   * the '.toList' which is an immutable list instead of replacing it, therefor we've wrapped them
   * in a new Arraylist to provide mutability.
   */
  private static <R> List<R> toList(
      List<Integer> referenceDataIds, Function<Integer, R> newReferenceTypeFunction) {
    return new ArrayList<>(referenceDataIds.stream().map(newReferenceTypeFunction).toList());
  }

  private static <R> List<Integer> toIds(
      List<R> referenceData, Function<R, Integer> referenceTypeFunction) {
    return new ArrayList<>(referenceData.stream().map(referenceTypeFunction).toList());
  }
}
