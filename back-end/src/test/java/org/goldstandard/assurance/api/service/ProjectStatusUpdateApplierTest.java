package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.repository.ProjectStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectStatusUpdateApplierTest {

  private static final ZonedDateTime DATE = ZonedDateTime.now();

  @InjectMocks ProjectStatusUpdateApplier updater;

  @Mock ProjectStatusRepository repository;

  @Test
  void shouldUpdateAllRequestProjectsIfStatusIsBeforeNewStatus() {
    var newStatus = buildProjectStatus(20);
    givenProjectStatusFound(newStatus);
    var project1 = buildProject(1, buildProjectStatus(1));
    var project2 = buildProject(2, buildProjectStatus(2));
    var project3 = buildProject(3, buildProjectStatus(3));

    var projects =
        updater.updateProjectStatusesIfRequired(
            List.of(project1, project2, project3), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("projectStatus")
        .containsExactly(project1, project2, project3)
        .map(Project::getProjectStatus)
        .map(ReferenceDataType::getId)
        .containsExactly(20, 20, 20);
  }

  @Test
  void shouldNotUpdateAnyRequestProjectsIfStatusIsAfterNewStatus() {
    var newStatus = buildProjectStatus(1);
    givenProjectStatusFound(newStatus);
    var project1 = buildProject(1, buildProjectStatus(99));
    var project2 = buildProject(2, buildProjectStatus(100));
    var project3 = buildProject(3, buildProjectStatus(101));

    var projects =
        updater.updateProjectStatusesIfRequired(
            List.of(project1, project2, project3), newStatus.getId(), DATE);

    assertThat(projects)
        .containsExactly(project1, project2, project3)
        .map(Project::getProjectStatus)
        .map(ReferenceDataType::getId)
        .containsExactly(99, 100, 101);
  }

  @Test
  void shouldOnlyUpdateProjectStatusIfBeforeNewStatus() {
    var newStatus = buildProjectStatus(50);
    givenProjectStatusFound(newStatus);
    var project1 = buildProject(1, buildProjectStatus(49));
    var project2 = buildProject(2, buildProjectStatus(50));
    var project3 = buildProject(3, buildProjectStatus(51));

    var projects =
        updater.updateProjectStatusesIfRequired(
            List.of(project1, project2, project3), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("projectStatus")
        .containsExactly(project1, project2, project3)
        .map(Project::getProjectStatus)
        .map(ReferenceDataType::getId)
        .containsExactly(50, 50, 51);
  }

  @Test
  void shouldUpdateProjectListingDateIfMovingIntoListed() {
    var newStatus = buildProjectStatus(ProjectStatus.LISTED, 1);
    givenProjectStatusFound(newStatus);
    var project = buildProject(1, buildProjectStatus(ProjectStatus.NEW, 0));

    var projects =
        updater.updateProjectStatusesIfRequired(List.of(project), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("projectStatus", "listingDate")
        .containsOnly(project);
    var result = projects.stream().findFirst().orElseThrow();
    assertThat(result.getListingDate()).isEqualTo(DATE);
  }

  @Test
  void shouldUpdateDesignCertifiedDateIfMovingIntoDesignCertified() {
    var newStatus = buildProjectStatus(ProjectStatus.DESIGN_CERTIFIED, 1);
    givenProjectStatusFound(newStatus);
    var project = buildProject(1, buildProjectStatus(ProjectStatus.NEW, 0));
    project.setListingDate(DATE.minusDays(1));

    var projects =
        updater.updateProjectStatusesIfRequired(List.of(project), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
            "projectStatus", "designCertificationDate")
        .containsOnly(project);
    var result = projects.stream().findFirst().orElseThrow();
    assertThat(result.getDesignCertificationDate()).isEqualTo(DATE);
  }

  @Test
  void shouldUpdateCertifiedProjectDateIfMovingIntoCertifiedProject() {
    var newStatus = buildProjectStatus(ProjectStatus.CERTIFIED_PROJECT, 1);
    givenProjectStatusFound(newStatus);
    var project = buildProject(1, buildProjectStatus(ProjectStatus.NEW, 0));
    project.setListingDate(DATE.minusDays(2));
    project.setDesignCertificationDate(DATE.minusDays(1));
    var projects =
        updater.updateProjectStatusesIfRequired(List.of(project), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
            "projectStatus", "certifiedProjectDate")
        .containsOnly(project);
    var result = projects.stream().findFirst().orElseThrow();
    assertThat(result.getCertifiedProjectDate()).isEqualTo(DATE);
  }

  @Test
  void shouldUpdateAllProjectDatesIfSkippingStraightToCertifiedProject() {
    var newStatus = buildProjectStatus(ProjectStatus.CERTIFIED_PROJECT, 1);
    givenProjectStatusFound(newStatus);
    var project = buildProject(1, buildProjectStatus(ProjectStatus.NEW, 0));

    var projects =
        updater.updateProjectStatusesIfRequired(List.of(project), newStatus.getId(), DATE);

    assertThat(projects)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
            "projectStatus", "listingDate", "designCertificationDate", "certifiedProjectDate")
        .containsOnly(project);
    var result = projects.stream().findFirst().orElseThrow();
    assertThat(result.getListingDate()).isEqualTo(DATE);
    assertThat(result.getDesignCertificationDate()).isEqualTo(DATE);
    assertThat(result.getCertifiedProjectDate()).isEqualTo(DATE);
  }

  private void givenProjectStatusFound(ProjectStatus status) {
    when(repository.forceFindById(status.getId())).thenReturn(status);
  }

  private static Project buildProject(int id, ProjectStatus status) {
    return Project.builder().id(id).projectStatus(status).build();
  }

  private static ProjectStatus buildProjectStatus(int sequence) {
    return buildProjectStatus(sequence, sequence);
  }

  private static ProjectStatus buildProjectStatus(int id, int sequence) {
    var status = new ProjectStatus(id);
    status.setSequence(sequence);
    return status;
  }
}
