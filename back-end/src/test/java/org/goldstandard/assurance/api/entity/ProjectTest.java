package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectTest {

  @Test
  void shouldExportAllMethodologiesEvenWhenDuplicatesArePresent() {
    var methodology = new Methodology(0);
    var creditingPeriods = List.of(toCreditingPeriod(methodology), toCreditingPeriod(methodology));
    var project = Project.builder().creditingPeriods(creditingPeriods).build();

    var allMethodologies = project.getAllMethodologies();

    assertThat(allMethodologies).containsExactly(methodology);
  }

  @Test
  void shouldReturnTrueIfBothProjectsHaveTheSameMaster() {
    var project1 = Project.builder().masterProject(ProjectMother.withIdOnly(1)).build();
    var project2 = Project.builder().masterProject(ProjectMother.withIdOnly(1)).build();

    var sameMaster = project1.hasSameMasterAs(project2);

    assertThat(sameMaster).isTrue();
  }

  @Test
  void shouldReturnFalseIfBothProjectsHaveDifferentMasters() {
    var project1 = Project.builder().masterProject(ProjectMother.withIdOnly(1)).build();
    var project2 = Project.builder().masterProject(ProjectMother.withIdOnly(2)).build();

    var sameMaster = project1.hasSameMasterAs(project2);

    assertThat(sameMaster).isFalse();
  }

  @Test
  void shouldReturnIsNewFalseIfProjectStatusIsNull() {
    var project = Project.builder().projectStatus(null).build();

    var isNew = project.isNew();

    assertThat(isNew).isFalse();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldReturnIsNewFromProjectStatus(boolean statusIsNew) {
    var status = mock(ProjectStatus.class);
    when(status.isNew()).thenReturn(statusIsNew);
    var project = Project.builder().projectStatus(status).build();

    var isNew = project.isNew();

    assertThat(isNew).isEqualTo(statusIsNew);
  }

  @Test
  void shouldReturnNextCreditingPeriodId() {
    var project = Project.builder().creditingPeriods(Collections.emptyList()).build();

    var nextId = project.getNextCreditingPeriodId();

    assertThat(nextId).isOne();
  }

  @Test
  void shouldReturnInLeastDevelopedCountryFalseIfProjectLocationsIsNull() {
    var project = Project.builder().projectLocations(null).build();

    var ldc = project.inLeastDevelopedCountry();

    assertThat(ldc).isFalse();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldReturnInLeastDevelopedCountryFromProjectLocations(boolean locationLdc) {
    var location = mock(ProjectLocation.class);
    when(location.isLeastDevelopedCountry()).thenReturn(locationLdc);
    var project = Project.builder().projectLocations(List.of(location)).build();

    var ldc = project.inLeastDevelopedCountry();

    assertThat(ldc).isEqualTo(locationLdc);
  }

  private static ProjectCreditingPeriod toCreditingPeriod(Methodology methodology) {
    var cpMethodology =
        ProjectCreditingPeriodMethodology.builder()
            .methodologyId(methodology.getId())
            .methodology(methodology)
            .build();
    return ProjectCreditingPeriod.builder().methodologies(List.of(cpMethodology)).build();
  }
}
