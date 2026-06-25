package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectAccessPolicyTest {

  @InjectMocks private ProjectAccessPolicy policy;

  @Mock private PermissionsService permissionsService;

  @Mock private AssignmentPermissionsService assignmentPermissionsService;

  @Test
  void shouldNotThrowExceptionIfProjectStatusIsNotNew() {
    Project project = buildProjectWithStatus(new ProjectStatus(0));

    ThrowingCallable call = () -> policy.validateUserCanViewProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfUserIsProjectOwner() {
    Project project = buildProject();
    when(permissionsService.isProjectOwner(project)).thenReturn(true);

    ThrowingCallable call = () -> policy.validateUserCanViewProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfUserIsAdminOrFinanceOrArmOrTacOrRegistry() {
    Project project = buildProject();
    when(permissionsService.isAdminOrFinanceOrArmOrTacOrRegistry()).thenReturn(true);

    ThrowingCallable call = () -> policy.validateUserCanViewProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldNotThrowExceptionIfUserIsReviewerOrVVBAndHasRequestsAssignedToProjectAccount() {
    Project project = buildProject();
    when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
    when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
        .thenReturn(true);

    ThrowingCallable call = () -> policy.validateUserCanViewProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void
      shouldNotThrowExceptionIfUserIsReviewerOrVVBAndDoesNotHaveRequestsAssignedToProjectAccount() {
    Project project = buildProject();
    when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
    when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
        .thenReturn(false);

    Throwable error = catchThrowable(() -> policy.validateUserCanViewProject(project));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void
      shouldThrowExceptionIfUserIsNotOwnerOrAdminOrFinanceOrArmOrTacOrRegistryOrAssignedReviewerOrVVB() {
    Project project = buildProject();
    when(permissionsService.isProjectOwner(project)).thenReturn(false);
    when(permissionsService.isAdminOrFinanceOrArmOrTacOrRegistry()).thenReturn(false);
    when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(false);

    Throwable error = catchThrowable(() -> policy.validateUserCanViewProject(project));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void shouldResolveRequestedAccountForAdminOrArmProjectMutation() {
    when(permissionsService.isAdminOrArm()).thenReturn(true);

    var accountId = policy.resolveProjectAccountId(42);

    assertThat(accountId).isEqualTo(42);
  }

  @Test
  void shouldResolveAuthenticatedAccountForProjectDeveloperProjectMutation() {
    when(permissionsService.isAdminOrArm()).thenReturn(false);
    when(permissionsService.isProjectDeveloper()).thenReturn(true);
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.of(7));

    var accountId = policy.resolveProjectAccountId(null);

    assertThat(accountId).isEqualTo(7);
  }

  @Test
  void shouldRejectProjectDeveloperProjectMutationForAnotherAccount() {
    when(permissionsService.isAdminOrArm()).thenReturn(false);
    when(permissionsService.isProjectDeveloper()).thenReturn(true);
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.of(7));

    var error = catchThrowable(() -> policy.resolveProjectAccountId(8));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void shouldAllowAdminOrArmToUpdateAnyProjectStatus() {
    Project listedProject = buildProjectWithStatus(new ProjectStatus(ProjectStatus.LISTED));
    when(permissionsService.isProjectOwner(listedProject)).thenReturn(false);
    when(permissionsService.isAdminOrArm()).thenReturn(true);

    ThrowingCallable call = () -> policy.validateCanUpdateProject(listedProject);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldAllowProjectDeveloperOwnerToUpdateNewProject() {
    Project project = buildProject();
    when(permissionsService.isProjectOwner(project)).thenReturn(true);

    ThrowingCallable call = () -> policy.validateCanUpdateProject(project);

    assertThatCode(call).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectProjectDeveloperOwnerUpdatingListedProject() {
    Project project = buildProjectWithStatus(new ProjectStatus(ProjectStatus.LISTED));
    when(permissionsService.isProjectOwner(project)).thenReturn(true);
    when(permissionsService.isAdminOrArm()).thenReturn(false);

    Throwable error = catchThrowable(() -> policy.validateCanUpdateProject(project));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  @Test
  void shouldRejectProjectDeveloperUpdatingNonOwnedProject() {
    Project project = buildProject();
    when(permissionsService.isProjectOwner(project)).thenReturn(false);
    when(permissionsService.isAdminOrArm()).thenReturn(false);

    Throwable error = catchThrowable(() -> policy.validateCanUpdateProject(project));

    assertThat(error).isInstanceOf(PermissionException.class);
  }

  private static Project buildProject() {
    return buildProjectWithStatus(new ProjectStatus(ProjectStatus.NEW));
  }

  private static Project buildProjectWithStatus(ProjectStatus status) {
    return Project.builder().id(123).gsid("GS123").projectStatus(status).build();
  }
}
