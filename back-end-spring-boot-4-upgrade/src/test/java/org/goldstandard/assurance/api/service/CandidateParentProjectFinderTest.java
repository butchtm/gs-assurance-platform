package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.projection.ProjectMinimal;
import org.goldstandard.assurance.api.repository.ProjectClassificationRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandidateParentProjectFinderTest {

  @InjectMocks CandidateParentProjectFinder finder;

  @Mock ProjectRepository projectRepository;

  @Mock ProjectClassificationRepository classificationRepository;

  @Mock PermissionsService permissionsService;

  @Test
  void shouldNotReturnCandidatePOAsIfNotRealCaseOrRegularCaseOrLegacyVPAClassification() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(2)
            .classificationId(classification.getId())
            .build();
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEmpty();
    verifyNoInteractions(projectRepository);
  }

  @Test
  void shouldReturnAllAccountPOAsForRealOrRegularCaseClassificationForNonAdminOrArmUser() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(3)
            .classificationId(classification.getId())
            .build();
    when(classification.isRealCaseVpa()).thenReturn(true);
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(permissionsService.isAdminOrArm()).thenReturn(false);
    var expectedProjects = List.of(mock(ProjectMinimal.class));
    when(projectRepository.findAllPOAsByAccountId(request.getAccountId()))
        .thenReturn(expectedProjects);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldReturnAllPOAsForRealOrRegularCaseClassificationForAdminOrArmUser() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder().classificationId(classification.getId()).build();
    when(classification.isRealCaseVpa()).thenReturn(true);
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(permissionsService.isAdminOrArm()).thenReturn(true);
    var expectedProjects = List.of(mock(ProjectMinimal.class));
    when(projectRepository.findAllPOAs()).thenReturn(expectedProjects);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldReturnOnlyAccountPOAsWithLegacyVpaCpaChildrenForLegacyVpaCpaCaseClassification() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(5)
            .classificationId(classification.getId())
            .build();
    when(classification.isLegacyVpaCpa()).thenReturn(true);
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    var project1 = givenProjectWithId(6);
    var project2 = givenProjectWithId(7);
    when(projectRepository.findAllPOAsByAccountId(request.getAccountId()))
        .thenReturn(List.of(project1, project2));
    when(projectRepository.existsByMasterProjectIdAndProjectClassificationId(
            project1.getId(), classification.getId()))
        .thenReturn(false);
    when(projectRepository.existsByMasterProjectIdAndProjectClassificationId(
            project2.getId(), classification.getId()))
        .thenReturn(true);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).containsExactly(project2);
  }

  @Test
  void shouldReturnCandidateRealCaseVPAs() {
    var request = GetCandidateRealCaseVPAsRequest.builder().accountId(1).masterProjectId(2).build();
    var expectedProjects = List.of(mock(Project.class));
    when(projectRepository.findAllRealCaseVPAsByAccountIdAndMasterProjectId(
            request.getAccountId(), request.getMasterProjectId()))
        .thenReturn(expectedProjects);

    var projects = finder.getCandidateRealCaseVPAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  private ProjectClassification givenClassification() {
    var classification = mock(ProjectClassification.class);
    when(classification.getId()).thenReturn(1);
    return classification;
  }

  private ProjectMinimal givenProjectWithId(int id) {
    var project = mock(ProjectMinimal.class);
    when(project.getId()).thenReturn(id);
    return project;
  }
}
