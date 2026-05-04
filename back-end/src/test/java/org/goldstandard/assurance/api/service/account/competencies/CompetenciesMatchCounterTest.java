package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetenciesMatchCounterTest {

  @Test
  void shouldReturnZeroIfCompetenciesAreNull() {
    Request request = requestBuilder().build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(null);

    long count = counter.applyAsLong(request);

    assertThat(count).isZero();
  }

  @Test
  void shouldReturnZeroIfNoMatches() {
    Request request = requestBuilder().build();
    Competencies competencies = competenciesBuilder().build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isZero();
  }

  @Test
  void shouldCountEmissionsMatch() {
    Request request = requestBuilder().emissionReductionAmount(10).build();
    Competencies competencies =
        competenciesBuilder().estimatedAnnualEmissionReductionAmount(10).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectTypeMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().projectTypes(project.getProjectTypes()).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectLocationMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().methodologies(project.getMethodologies()).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectAssetsMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().certifiedProducts(project.getProjectAssets()).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectMethodologiesMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().projectLocations(project.getProjectLocations()).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountSustainableDevelopmentGoalsMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder()
            .sustainableDevelopmentGoals(project.getSustainableDevelopmentGoals())
            .build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectSizeMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().projectScales(List.of(project.getProjectSize())).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountRequestTypeMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder().requestTypes(List.of(request.getRequestType())).build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  @Test
  void shouldCountProjectDeveloperMatch() {
    var project = buildProject();
    var request = requestBuilder().project(project).build();
    Competencies competencies =
        competenciesBuilder()
            .projectDevelopers(List.of(new Account(project.getAccountId())))
            .build();
    CompetenciesMatchCounter counter = new CompetenciesMatchCounter(competencies);

    long count = counter.applyAsLong(request);

    assertThat(count).isOne();
  }

  private static Project buildProject() {
    var project = new Project();
    project.setGsid("GS1");
    project.setProjectSize(new ProjectSize(1));
    project.setAccountId(1);
    project.setProjectTypes(List.of(new ProjectType(2)));
    project.setProjectLocations(List.of(new ProjectLocation(3)));
    project.setMethodologies(List.of(new Methodology(4)));
    project.setProjectAssets(List.of(new ProjectAsset(5)));
    project.setSustainableDevelopmentGoals(List.of(new SustainableDevelopmentGoal(6)));
    return project;
  }

  private static Request.RequestBuilder requestBuilder() {
    return Request.builder().project(buildProject()).requestType(new RequestType(1));
  }

  private static Competencies.CompetenciesBuilder competenciesBuilder() {
    return Competencies.builder()
        .id(1)
        .estimatedAnnualEmissionReductionAmount(null)
        .projectGsids(List.of())
        .certifiedProducts(List.of())
        .vvbsAssigned(List.of())
        .projectDevelopers(List.of())
        .sustainableDevelopmentGoals(List.of())
        .projectTypes(List.of())
        .projectLocations(List.of())
        .projectScales(List.of())
        .methodologies(List.of())
        .requestTypes(List.of());
  }
}
