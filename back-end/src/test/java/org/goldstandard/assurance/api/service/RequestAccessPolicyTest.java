package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAccessPolicyTest {

  @InjectMocks RequestAccessPolicy policy;

  @Mock PermissionsService permissionsService;

  @Mock AssignmentPermissionsService assignmentPermissionsService;

  @Nested
  class CreateRequestTest {

    @Test
    void shouldNotThrowExceptionIfUserCanAccessAllProjects() {
      var project = buildProject();
      when(permissionsService.canAccessAllProjects()).thenReturn(true);

      ThrowingCallable call = () -> policy.validateCanCreateRequest(project);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowExceptionIfUserIsProjectOwner() {
      var project = buildProject();
      when(permissionsService.isProjectOwner(project)).thenReturn(true);

      ThrowingCallable call = () -> policy.validateCanCreateRequest(project);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionIfUserCannotAccessAllProjectsAndIsNotProjectOwner() {
      var project = buildProject();
      when(permissionsService.canAccessAllProjects()).thenReturn(false);
      when(permissionsService.isProjectOwner(project)).thenReturn(false);

      Throwable error = catchThrowable(() -> policy.validateCanCreateRequest(project));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage("Not permitted to create request for project %d", project.getId());
    }
  }

  @Nested
  class UpdateRequestTest {

    @Test
    void shouldNotThrowExceptionIfUserCanAccessAllProjects() {
      when(permissionsService.canAccessAllProjects()).thenReturn(true);
      var project = buildProject();
      var request = buildRequest(project);

      ThrowingCallable call = () -> policy.validateCanUpdateRequest(request);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowExceptionIfUserIsProjectOwner() {
      var project = buildProject();
      var request = buildRequest(project);
      when(permissionsService.isProjectOwner(project)).thenReturn(true);

      ThrowingCallable call = () -> policy.validateCanUpdateRequest(request);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowExceptionIfUserIsAssignedToRequest() {
      var project = buildProject();
      var request = buildRequest(project);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(request.getId()))
          .thenReturn(true);

      ThrowingCallable call = () -> policy.validateCanUpdateRequest(request);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void
        shouldThrowExceptionIfUserCannotAccessAllProjectsAndIsNotProjectOwnerAndIsNotAssignedToRequest() {
      var project = buildProject();
      var request = buildRequest(project);
      when(permissionsService.canAccessAllProjects()).thenReturn(false);
      when(permissionsService.isProjectOwner(project)).thenReturn(false);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(request.getId()))
          .thenReturn(false);

      Throwable error = catchThrowable(() -> policy.validateCanUpdateRequest(request));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage(
              "Not permitted to update request %d for project %d",
              request.getId(), project.getId());
    }
  }

  private static Project buildProject() {
    return Project.builder().id(123).accountId(456).build();
  }

  private static Request buildRequest(Project project) {
    return Request.builder().id(789).project(project).build();
  }
}
