package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.ProjectCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.external.ExternalUpdateExecutor;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  private static final Integer TEST_LOGGED_IN_USER_ID = 348543;
  private static final Integer TEST_PROJECT_STATUS_ID = 3;

  @InjectMocks ProjectService projectService;

  @Mock ProjectFinder finder;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Mock ProjectAccessPolicy accessPolicy;

  @Mock ProjectTransaction transaction;

  @Mock ExternalUpdateExecutor externalUpdateExecutor;

  @Mock ProjectCurrentRequestUpdater currentRequestUpdater;

  @Mock CreditingPeriodFactory creditingPeriodFactory;

  @Mock ProjectUpdateApplier updateApplier;

  @Mock CandidateParentEligibilityPolicy parentEligibilityPolicy;

  @Captor ArgumentCaptor<Project> savedProjectCaptor;

  @Test
  void getProjects() {
    Pageable pageable = Pageable.ofSize(1);
    ProjectSearchCriteriaDTO criteria = ProjectSearchCriteriaDTO.builder().build();
    Page<Project> expectedPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    when(finder.getProjects(pageable, criteria)).thenReturn(expectedPage);

    var page = projectService.getProjects(pageable, criteria);

    assertThat(page).isEqualTo(expectedPage);
  }

  @Test
  void createProjectSetsUpdatedByUserId() {
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId())
        .thenReturn(TEST_LOGGED_IN_USER_ID);
    var create = buildProjectCreateDTO();
    var creditingPeriods = givenCreditingPeriods(create.getCreditingPeriods());
    when(transaction.performCreate(any(Project.class), eq(creditingPeriods), eq(null)))
        .thenReturn(new Project());

    projectService.createProject(create);

    verify(transaction).performCreate(savedProjectCaptor.capture(), eq(creditingPeriods), eq(null));
    var savedProject = savedProjectCaptor.getValue();
    assertThat(savedProject.getUpdatedByUserId()).isEqualTo(TEST_LOGGED_IN_USER_ID);
  }

  @Test
  void createProjectSetsProjectStatus() {
    var create = buildProjectCreateDTO();
    var creditingPeriods = givenCreditingPeriods(create.getCreditingPeriods());
    when(transaction.performCreate(any(Project.class), eq(creditingPeriods), eq(null)))
        .thenReturn(new Project());

    projectService.createProject(create);

    verify(transaction).performCreate(savedProjectCaptor.capture(), eq(creditingPeriods), eq(null));
    var savedProject = savedProjectCaptor.getValue();
    assertThat(savedProject.getProjectStatus().getId()).isEqualTo(TEST_PROJECT_STATUS_ID);
  }

  @Test
  void createProjectTriggersStatusUpdateToExternalSystems() {
    var create = buildProjectCreateDTO();
    var savedProject = buildProject();
    var creditingPeriods = givenCreditingPeriods(create.getCreditingPeriods());
    when(transaction.performCreate(any(Project.class), eq(creditingPeriods), eq(null)))
        .thenReturn(savedProject);

    projectService.createProject(create);

    verify(externalUpdateExecutor).executeAsync(savedProject.getId());
  }

  @Test
  void createProjectValidatesVpaParentEligibilityBeforeSaving() {
    var effectiveAccountId = 44;
    var create = buildProjectCreateDTO();
    create.setAccountId(effectiveAccountId);
    create.setProjectClassificationId(ProjectClassification.REAL_CASE_VPA_ID);
    create.setMasterProjectId(10);
    create.setParentProjectId(10);
    var parentProject =
        Project.builder()
            .id(10)
            .accountId(effectiveAccountId)
            .projectClassification(ProjectClassification.programmeOfActivities())
            .build();
    var creditingPeriods = givenCreditingPeriods(create.getCreditingPeriods());
    when(accessPolicy.resolveProjectAccountId(create.getAccountId()))
        .thenReturn(effectiveAccountId);
    when(finder.forceGetProjectById(parentProject.getId())).thenReturn(parentProject);
    when(transaction.performCreate(any(Project.class), eq(creditingPeriods), eq(null)))
        .thenReturn(new Project());

    projectService.createProject(create);

    verify(parentEligibilityPolicy)
        .validateParentEligibility(
            eq(effectiveAccountId),
            any(ProjectClassification.class),
            eq(parentProject),
            eq(parentProject));
  }

  @Test
  void createProjectDoesNotSaveWhenVpaParentEligibilityFails() {
    var effectiveAccountId = 44;
    var create = buildProjectCreateDTO();
    create.setAccountId(effectiveAccountId);
    create.setProjectClassificationId(ProjectClassification.REAL_CASE_VPA_ID);
    create.setMasterProjectId(10);
    create.setParentProjectId(10);
    var parentProject =
        Project.builder()
            .id(10)
            .accountId(effectiveAccountId)
            .projectClassification(ProjectClassification.programmeOfActivities())
            .build();
    var expectedError = new BadRequestException("invalid parent");
    when(accessPolicy.resolveProjectAccountId(create.getAccountId()))
        .thenReturn(effectiveAccountId);
    when(finder.forceGetProjectById(parentProject.getId())).thenReturn(parentProject);
    doThrow(expectedError)
        .when(parentEligibilityPolicy)
        .validateParentEligibility(
            eq(effectiveAccountId),
            any(ProjectClassification.class),
            eq(parentProject),
            eq(parentProject));

    var error = catchThrowable(() -> projectService.createProject(create));

    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(transaction);
  }

  @Test
  void shouldUpdateProject() {
    int projectId = 1;
    var originalProject = buildProjectWithImmutableFields(projectId, 3, 4, 5);
    when(finder.forceGetProjectById(projectId)).thenReturn(originalProject);
    var update = buildProjectUpdateWithImmutableFields(originalProject);
    var creditingPeriods = givenCreditingPeriods(update.getCreditingPeriods());
    var updatedProject = mock(Project.class);
    when(updateApplier.applyUpdates(originalProject, update)).thenReturn(updatedProject);
    when(transaction.performUpdate(originalProject, updatedProject, creditingPeriods, null))
        .thenReturn(List.of(new Project()));

    projectService.updateProject(projectId, update);

    verify(transaction)
        .performUpdate(
            eq(originalProject), savedProjectCaptor.capture(), eq(creditingPeriods), eq(null));
    assertThat(savedProjectCaptor.getValue()).isEqualTo(updatedProject);
  }

  @Test
  void updateProjectTriggersUpdateToExternalSystems() {
    int projectId = 1;
    var originalProject = Project.builder().id(projectId).build();
    when(finder.forceGetProjectById(projectId)).thenReturn(originalProject);
    var update = ProjectUpdateDTO.builder().build();
    var updatedProject = mock(Project.class);
    when(updateApplier.applyUpdates(originalProject, update)).thenReturn(updatedProject);
    var savedProject = buildProject(projectId);
    var creditingPeriods = givenCreditingPeriods(update.getCreditingPeriods());
    var savedProjects = List.of(savedProject);
    when(transaction.performUpdate(originalProject, updatedProject, creditingPeriods, null))
        .thenReturn(savedProjects);

    projectService.updateProject(projectId, update);

    verify(externalUpdateExecutor).executeAsync(savedProjects);
  }

  @Test
  void updateProjectRejectsAccountChangeBeforeApplyingUpdates() {
    int projectId = 1;
    var originalProject = buildProjectWithImmutableFields(projectId, 3, 4, 5);
    var update =
        ProjectUpdateDTO.builder()
            .accountId(99)
            .projectClassificationId(originalProject.getProjectClassification().getId())
            .masterProjectId(originalProject.getMasterProject().getId())
            .parentProjectId(originalProject.getParentProject().getId())
            .build();
    when(finder.forceGetProjectById(projectId)).thenReturn(originalProject);

    var error = catchThrowable(() -> projectService.updateProject(projectId, update));

    assertThat(error).isInstanceOf(BadRequestException.class).hasMessageContaining("accountId");
    verifyNoInteractions(updateApplier, transaction);
  }

  @Test
  void updateProjectRejectsHierarchyChangeBeforeApplyingUpdates() {
    int projectId = 1;
    var originalProject = buildProjectWithImmutableFields(projectId, 3, 4, 5);
    var update =
        ProjectUpdateDTO.builder()
            .accountId(originalProject.getAccountId())
            .projectClassificationId(originalProject.getProjectClassification().getId())
            .masterProjectId(99)
            .parentProjectId(originalProject.getParentProject().getId())
            .build();
    when(finder.forceGetProjectById(projectId)).thenReturn(originalProject);

    var error = catchThrowable(() -> projectService.updateProject(projectId, update));

    assertThat(error)
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("masterProjectId");
    verifyNoInteractions(updateApplier, transaction);
  }

  @Test
  void shouldReturnProjectByGsId() {
    var gsId = "GS10001";
    var expectedProject = mock(Project.class);
    when(finder.forceGetProjectByGsId(gsId)).thenReturn(expectedProject);

    var project = projectService.getProjectByGsId(gsId);

    assertThat(project).isEqualTo(expectedProject);
  }

  @Test
  void shouldThrowExceptionIfProjectAccessNotAllowedByGsId() {
    var gsId = "GS10003";
    var project = mock(Project.class);
    when(finder.forceGetProjectByGsId(gsId)).thenReturn(project);
    RuntimeException expectedError = new PermissionException("Boom!");
    doThrow(expectedError).when(accessPolicy).validateUserCanViewProject(project);

    var error = catchThrowable(() -> projectService.getProjectByGsId(gsId));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldReturnProjectById() {
    var id = 1;
    var expectedProject = mock(Project.class);
    when(finder.forceGetProjectById(id)).thenReturn(expectedProject);

    var project = projectService.getProjectById(id);

    assertThat(project).isEqualTo(expectedProject);
  }

  @Test
  void shouldThrowExceptionIfProjectAccessNotAllowedById() {
    var id = 3;
    var project = mock(Project.class);
    when(finder.forceGetProjectById(id)).thenReturn(project);
    RuntimeException expectedError = new PermissionException("Boom!");
    doThrow(expectedError).when(accessPolicy).validateUserCanViewProject(project);

    var error = catchThrowable(() -> projectService.getProjectById(id));

    assertThat(error).isEqualTo(expectedError);
  }

  private static ProjectCreateDTO buildProjectCreateDTO() {
    var create = new ProjectCreateDTO();
    create.setProjectStatusId(TEST_PROJECT_STATUS_ID);
    return create;
  }

  private static Project buildProject() {
    return buildProject(999);
  }

  private static Project buildProject(int id) {
    return Project.builder().id(id).build();
  }

  private static Project buildProjectWithImmutableFields(
      int projectId, int accountId, int masterProjectId, int parentProjectId) {
    return Project.builder()
        .id(projectId)
        .accountId(accountId)
        .projectClassification(ProjectClassification.realCaseVpa())
        .masterProject(new Project(masterProjectId))
        .parentProject(new Project(parentProjectId))
        .build();
  }

  private static ProjectUpdateDTO buildProjectUpdateWithImmutableFields(Project project) {
    return ProjectUpdateDTO.builder()
        .accountId(project.getAccountId())
        .projectClassificationId(project.getProjectClassification().getId())
        .masterProjectId(project.getMasterProject().getId())
        .parentProjectId(project.getParentProject().getId())
        .build();
  }

  private List<ProjectCreditingPeriod> givenCreditingPeriods(List<CreditingPeriodDTO> dtos) {
    var creditingPeriods = List.of(mock(ProjectCreditingPeriod.class));
    when(creditingPeriodFactory.toCreditingPeriods(any(Project.class), eq(dtos)))
        .thenReturn(creditingPeriods);
    return creditingPeriods;
  }
}
