package org.goldstandard.assurance.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.converter.RequestConverter;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.fixtures.RequestCreateUpdateDTOMother;
import org.goldstandard.assurance.api.service.ProjectAccessPolicy;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestAccessPolicy;
import org.goldstandard.assurance.api.service.RequestCreator;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestFacadeTest {

  private static final int UNREAD_MESSAGE_COUNT = 0;

  @InjectMocks RequestFacade requestFacade;

  @Mock RequestFinder requestFinder;

  @Mock ProjectFinder projectFinder;

  @Mock RequestAccessPolicy requestAccessPolicy;

  @Mock RequestCreator creator;

  @Mock RequestConverter requestConverter;

  @Mock ProjectAccessPolicy projectAccessPolicy;

  @Mock UnreadMessageFinder unreadMessageFinder;

  @Test
  void shouldThrowExceptionIfNotPermittedToCreateRequestForProject() {
    var project = buildProject();
    when(projectFinder.forceGetProjectById(project.getId())).thenReturn(project);
    var expectedError = new PermissionException("permission-exception");
    doThrow(expectedError).when(requestAccessPolicy).validateCanCreateRequest(project);
    var create = RequestCreateUpdateDTOMother.build();

    Throwable error =
        catchThrowable(() -> requestFacade.createAndSaveRequest(project.getId(), create));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void shouldCreateRequestIfPermitted() {
    var project = buildProject();
    when(projectFinder.forceGetProjectById(project.getId())).thenReturn(project);
    var create = RequestCreateUpdateDTOMother.build();
    var expectedRequest = buildRequest();
    when(creator.create(project.getId(), create)).thenReturn(expectedRequest);

    var request = requestFacade.createAndSaveRequest(project.getId(), create);

    assertThat(request).isEqualTo(expectedRequest);
  }

  @Test
  void shouldThrowNotFoundRequestIfThrownByFinder() {
    int requestId = 2;
    var expectedError = new NotFoundException("not-found-exception");
    when(requestFinder.getRequestById(requestId)).thenThrow(expectedError);

    Throwable error = catchThrowable(() -> requestFacade.getRequestDetailById(requestId));

    assertThat(error).isEqualTo(expectedError);
  }

  @Test
  void
      shouldThrowPermissionsExceptionIfUserCannotAccessProjectAndRequestNotAssignedToUserAccount() {
    var request = givenRequestFoundById();
    var expectedException = new PermissionException("access not permitted");
    doThrow(expectedException)
        .when(projectAccessPolicy)
        .validateUserCanViewProject(request.getProject());

    Throwable error = catchThrowable(() -> requestFacade.getRequestDetailById(request.getId()));

    assertThat(error).isEqualTo(expectedException);
  }

  @Test
  void shouldReturnRequestIfUserCanAccessRequestProject() {
    var expectedDto = new RequestDTO();
    var request = givenRequestFoundById();
    var unreadMessageCount = givenUnreadMessageCountFound(request.getId());
    when(requestConverter.toRequestDetailDTO(request, unreadMessageCount)).thenReturn(expectedDto);

    var result = requestFacade.getRequestDetailById(request.getId());

    assertThat(result).isEqualTo(expectedDto);
  }

  @Test
  void shouldReturnRequestIfRequestIsAssignedToUserAccount() {
    var expectedDto = new RequestDTO();
    var request = givenRequestFoundById();
    var unreadMessageCount = givenUnreadMessageCountFound(request.getId());
    when(requestConverter.toRequestDetailDTO(request, unreadMessageCount)).thenReturn(expectedDto);

    var result = requestFacade.getRequestDetailById(request.getId());

    assertThat(result).isEqualTo(expectedDto);
  }

  private int givenUnreadMessageCountFound(int requestId) {
    var unreadMessageCount = 1;
    when(unreadMessageFinder.getUnreadMessageCountForAuthenticatedUserAndRequest(requestId))
        .thenReturn(unreadMessageCount);
    return unreadMessageCount;
  }

  private Request givenRequestFoundById() {
    var request = buildRequest();
    when(requestFinder.getRequestById(request.getId())).thenReturn(request);
    return request;
  }

  private static Request buildRequest() {
    return Request.builder().id(989).project(buildProject()).build();
  }

  private static Project buildProject() {
    var project = new Project();
    project.setId(565);
    return project;
  }
}
