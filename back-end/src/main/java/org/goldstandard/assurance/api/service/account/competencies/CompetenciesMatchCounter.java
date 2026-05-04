package org.goldstandard.assurance.api.service.account.competencies;

import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.hasAccountMatch;
import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.hasMatch;
import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.isEmissionsMatch;
import static org.goldstandard.assurance.api.service.account.competencies.CompetenciesMatchCountUtils.isMatch;

import java.util.Objects;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Request;

@RequiredArgsConstructor
public class CompetenciesMatchCounter implements ToLongFunction<Request> {

  private final Competencies competencies;

  @Override
  public long applyAsLong(Request request) {
    if (Objects.isNull(competencies)) {
      return 0L;
    }
    var project = request.getProject();
    long count = 0;
    if (isEmissionsMatch(
        competencies.getEstimatedAnnualEmissionReductionAmount(),
        request.getEmissionReductionAmount())) {
      count += 1;
    }
    if (hasMatch(competencies.getProjectTypes(), project.getProjectTypes())) {
      count += 1;
    }
    if (hasMatch(competencies.getProjectLocations(), project.getProjectLocations())) {
      count += 1;
    }
    if (hasMatch(competencies.getMethodologies(), project.getMethodologies())) {
      count += 1;
    }
    if (hasMatch(competencies.getCertifiedProducts(), project.getProjectAssets())) {
      count += 1;
    }
    if (hasMatch(
        competencies.getSustainableDevelopmentGoals(), project.getSustainableDevelopmentGoals())) {
      count += 1;
    }
    if (isMatch(competencies.getProjectScales(), project.getProjectSize())) {
      count += 1;
    }
    if (isMatch(competencies.getRequestTypes(), request.getRequestType())) {
      count += 1;
    }
    if (hasAccountMatch(competencies.getProjectDevelopers(), project.getAccountId())) {
      count += 1;
    }
    return count;
  }
}
