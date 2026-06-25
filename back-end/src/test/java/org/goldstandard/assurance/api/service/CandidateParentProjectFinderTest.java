package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateParentProjectDetailsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.PermissionException;
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

  @Mock ProjectAccessPolicy accessPolicy;

  @Mock CandidateParentEligibilityPolicy parentEligibilityPolicy;

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
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());

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
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    var expectedProjects = List.of(mock(ProjectMinimal.class));
    when(projectRepository.findAllPOAsByAccountId(request.getAccountId()))
        .thenReturn(expectedProjects);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldReturnAllPOAsForNativeRealCaseClassificationForAdminOrArmUser() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(3)
            .classificationId(classification.getId())
            .build();
    when(classification.isRealCaseVpa()).thenReturn(true);
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    when(permissionsService.isAdminOrArm()).thenReturn(true);
    var expectedProjects = List.of(mock(ProjectMinimal.class));
    when(projectRepository.findAllPOAs()).thenReturn(expectedProjects);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldReturnAllPOAsForCrossLinkClassification() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(3)
            .classificationId(classification.getId())
            .build();
    when(classification.isRealCaseVpa()).thenReturn(true);
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    when(parentEligibilityPolicy.canUseCrossAccountParents(classification)).thenReturn(true);
    var expectedProjects = List.of(mock(ProjectMinimal.class));
    when(projectRepository.findAllPOAs()).thenReturn(expectedProjects);

    var projects = finder.getCandidatePOAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldRejectCandidatePOAAccountMismatch() {
    var classification = givenClassification();
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(99)
            .classificationId(classification.getId())
            .build();
    var expectedError = new PermissionException("User not permitted to use project account 99");
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(accessPolicy.resolveProjectAccountId(request.getAccountId())).thenThrow(expectedError);

    var error = catchThrowable(() -> finder.getCandidatePOAs(request));

    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(projectRepository);
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
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
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
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    when(projectRepository.findAllRealCaseVPAsByAccountIdAndMasterProjectId(
            request.getAccountId(), request.getMasterProjectId()))
        .thenReturn(expectedProjects);

    var projects = finder.getCandidateRealCaseVPAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldReturnCrossAccountRealCaseVPAsUnderSelectedPOAForCrossLinkRegularVPA() {
    var request =
        GetCandidateRealCaseVPAsRequest.builder()
            .accountId(1)
            .masterProjectId(2)
            .classificationId(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID)
            .build();
    var expectedProjects = List.of(mock(Project.class));
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    when(projectRepository.findAllRealCaseVPAsByMasterProjectId(request.getMasterProjectId()))
        .thenReturn(expectedProjects);

    var projects = finder.getCandidateRealCaseVPAs(request);

    assertThat(projects).isEqualTo(expectedProjects);
  }

  @Test
  void shouldRejectCandidateRealCaseVPAAccountMismatch() {
    var request =
        GetCandidateRealCaseVPAsRequest.builder()
            .accountId(99)
            .masterProjectId(2)
            .classificationId(ProjectClassification.REGULAR_CASE_VPA_ID)
            .build();
    var expectedError = new PermissionException("User not permitted to use project account 99");
    when(accessPolicy.resolveProjectAccountId(request.getAccountId())).thenThrow(expectedError);

    var error = catchThrowable(() -> finder.getCandidateRealCaseVPAs(request));

    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(projectRepository);
  }

  @Test
  void shouldReturnCandidateParentDetailsAfterAccountAndEligibilityValidation() {
    var classification =
        new ProjectClassification(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);
    var request =
        GetCandidateParentProjectDetailsRequest.builder()
            .accountId(1)
            .classificationId(classification.getId())
            .candidateProjectId(2)
            .build();
    var expectedProject = Project.builder().id(request.getCandidateProjectId()).build();
    when(classificationRepository.forceFindById(request.getClassificationId()))
        .thenReturn(classification);
    when(accessPolicy.resolveProjectAccountId(request.getAccountId()))
        .thenReturn(request.getAccountId());
    when(projectRepository.findById(request.getCandidateProjectId()))
        .thenReturn(Optional.of(expectedProject));

    var project = finder.getCandidateParentDetails(request);

    assertThat(project).isEqualTo(expectedProject);
    verify(parentEligibilityPolicy)
        .validateCandidateParentDetails(
            request.getAccountId(), classification, expectedProject, null);
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
