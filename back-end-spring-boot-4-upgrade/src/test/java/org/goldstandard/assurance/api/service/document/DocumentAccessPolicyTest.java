package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.AssignmentPermissionsService;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentAccessPolicyTest {

  private static final int DOCUMENT_TYPE_ID = 1;

  @InjectMocks DocumentAccessPolicy documentAccessPolicy;

  @Mock ProjectFinder projectFinder;

  @Mock PermissionsService permissionsService;

  @Mock AssignmentPermissionsService assignmentPermissionsService;

  @Mock DocumentTypeUploadValidator uploadValidator;

  @Mock RequestFinder requestFinder;

  @Mock BatchRequestFinder batchRequestFinder;

  @Mock Project project;

  @Mock Request request;

  @Nested
  class DocumentManagement {
    @Test
    void shouldNotThrowExceptionIfUserIsAdminOrFinanceOrArmAndRequestIsActive() {
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(true);
      when(request.hasInactiveCurrentStatus()).thenReturn(false);

      ThrowingCallable call =
          () ->
              documentAccessPolicy.validateUserCanManageDocuments(
                  project, request, DOCUMENT_TYPE_ID);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void
        shouldNotThrowExceptionIfUserIsNotAdminOrFinanceOrArmButIsProjectOwnerAndRequestIsActive() {
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(false);
      when(permissionsService.isProjectOwner(project)).thenReturn(true);
      when(request.hasInactiveCurrentStatus()).thenReturn(false);

      ThrowingCallable call =
          () ->
              documentAccessPolicy.validateUserCanManageDocuments(
                  project, request, DOCUMENT_TYPE_ID);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void
        shouldNotThrowExceptionIfUserIsNotAdminOrFinanceOrArmButIsReviewerAndHasRequestAssignedToProjectAndRequestIsActive() {
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(false);
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
          .thenReturn(true);
      when(request.hasInactiveCurrentStatus()).thenReturn(false);

      ThrowingCallable call =
          () ->
              documentAccessPolicy.validateUserCanManageDocuments(
                  project, request, DOCUMENT_TYPE_ID);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowExceptionIfUserIsAdminOrArmAndRequestIsInactive() {
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(true);
      when(permissionsService.isAdminOrArm()).thenReturn(true);

      ThrowingCallable call =
          () ->
              documentAccessPolicy.validateUserCanManageDocuments(
                  project, request, DOCUMENT_TYPE_ID);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionIfUserIsNotAdminOrArmAndRequestIsInactive() {
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(true);
      when(permissionsService.isAdminOrArm()).thenReturn(false);
      when(request.hasInactiveCurrentStatus()).thenReturn(true);

      var error =
          catchThrowable(
              () ->
                  documentAccessPolicy.validateUserCanManageDocuments(
                      project, request, DOCUMENT_TYPE_ID));

      assertThat(error).hasMessage("user not permitted to manage documents for inactive requests");
    }

    @Test
    void shouldThrowExceptionIfUserIsNotAdminOrFinanceOrArmAndCannotAccessProject() {
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
          .thenReturn(true);
      var expectedError = new PermissionException("document type not permitted");
      doThrow(expectedError).when(uploadValidator).validateUserCanUpload(DOCUMENT_TYPE_ID);

      var error =
          catchThrowable(
              () ->
                  documentAccessPolicy.validateUserCanManageDocuments(
                      project, request, DOCUMENT_TYPE_ID));

      assertThat(error).isEqualTo(expectedError);
    }

    @Test
    void shouldThrowExceptionIfUserIsNotPermittedToUploadDocumentType() {
      when(project.getId()).thenReturn(1);
      when(permissionsService.isAdminOrFinanceOrArm()).thenReturn(false);

      var error =
          catchThrowable(
              () ->
                  documentAccessPolicy.validateUserCanManageDocuments(
                      project, request, DOCUMENT_TYPE_ID));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage("user not permitted to manage documents for project 1");
    }
  }

  @Nested
  class ViewPrivateDocuments {

    private static final int REQUEST_ID = 999;

    @Test
    void shouldBeAbleToViewPrivateDocumentsIfHaveCorrectRole() {
      var projectId = givenProjectIsFoundById();
      when(permissionsService.canAccessAllPrivateDocuments()).thenReturn(true);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldBeAbleToViewPrivateDocumentsIfProjectOwner() {
      var projectId = givenProjectIsFoundById();
      when(permissionsService.isProjectOwner(project)).thenReturn(true);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldBeAbleToViewPrivateDocumentsIfReviewerWithRequestAssignedToProject() {
      var projectId = givenProjectIsFoundById();
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
          .thenReturn(true);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldBeAbleToViewPrivateDocumentsIfReviewerWithRequestAssignedToBatchChildProject() {
      var projectId = givenProjectIsFoundById();
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
          .thenReturn(false);
      when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
      when(request.getId()).thenReturn(REQUEST_ID);
      when(request.isBatchParent()).thenReturn(true);
      var childProject = givenBatchChildProject(request);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(childProject))
          .thenReturn(true);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldBeAbleToViewPrivateDocumentsIfRequestAssignedToUserAccount() {
      var projectId = givenProjectIsFoundById();
      when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
      when(request.getId()).thenReturn(REQUEST_ID);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(REQUEST_ID))
          .thenReturn(true);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldNotBeAbleToViewPrivateDocumentsNotCorrectRoleAndRequestNotAssignedToAccount() {
      var projectId = givenProjectIsFoundById();
      when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
      when(request.getId()).thenReturn(REQUEST_ID);
      when(permissionsService.canAccessAllPrivateDocuments()).thenReturn(false);
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(false);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(REQUEST_ID))
          .thenReturn(false);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isFalse();
    }

    @Test
    void
        shouldNotBeAbleToViewPrivateDocumentsIfReviewerButDoesNotHaveRequestAssignedToUserAccount() {
      var projectId = givenProjectIsFoundById();
      when(requestFinder.getRequestById(REQUEST_ID)).thenReturn(request);
      when(permissionsService.isReviewerOrVVBOrRegistry()).thenReturn(true);
      when(assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project))
          .thenReturn(false);

      var result = documentAccessPolicy.canViewPrivateDocuments(projectId, REQUEST_ID);

      assertThat(result).isFalse();
    }

    private int givenProjectIsFoundById() {
      var projectId = 1;
      when(projectFinder.forceGetProjectById(projectId)).thenReturn(project);
      return projectId;
    }

    private Project givenBatchChildProject(Request request) {
      var childProject = mock(Project.class);
      when(batchRequestFinder.getChildProjects(request)).thenReturn(Stream.of(childProject));
      return childProject;
    }
  }

  @Nested
  class ViewReviewerUploadedDocuments {

    private static final int REQUEST_ID = 888;

    @Test
    void shouldAdminAndArmUsersShouldBeAbleToViewReviewerUploadedDocuments() {
      when(permissionsService.isAdminOrArm()).thenReturn(true);

      var result = documentAccessPolicy.canUserViewReviewerUploadedDocuments(REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldAssignedReviewersShouldBeAbleToViewReviewerUploadedDocuments() {
      when(permissionsService.isReviewer()).thenReturn(true);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(REQUEST_ID))
          .thenReturn(true);

      var result = documentAccessPolicy.canUserViewReviewerUploadedDocuments(REQUEST_ID);

      assertThat(result).isTrue();
    }

    @Test
    void shouldUnassignedReviewersShouldNotBeAbleToViewReviewerUploadedDocuments() {
      when(permissionsService.isReviewer()).thenReturn(true);
      when(assignmentPermissionsService.isRequestAssignedToUserAccount(REQUEST_ID))
          .thenReturn(false);

      var result = documentAccessPolicy.canUserViewReviewerUploadedDocuments(REQUEST_ID);

      assertThat(result).isFalse();
    }

    @Test
    void anyOtherUsersShouldNotBeAbleToViewReviewerUploadedDocuments() {
      when(permissionsService.isAdminOrArm()).thenReturn(false);
      when(permissionsService.isReviewer()).thenReturn(false);

      var result = documentAccessPolicy.canUserViewReviewerUploadedDocuments(REQUEST_ID);

      assertThat(result).isFalse();
    }
  }

  @Nested
  class ViewDocument {

    private static final int REQUEST_ID = 888;

    @Mock Request request;

    @Mock Document document;

    @BeforeEach
    void setUp() {
      when(request.getProject()).thenReturn(project);
    }

    @Test
    void authenticatedUserShouldNotBeAbleToViewPrivateDocumentsIfNotPermitted() {
      when(permissionsService.isAuthenticated()).thenReturn(true);
      when(request.getId()).thenReturn(REQUEST_ID);
      when(document.isPrivate()).thenReturn(true);
      when(permissionsService.canAccessAllPrivateDocuments()).thenReturn(false);

      Throwable error =
          catchThrowable(() -> documentAccessPolicy.validateCanViewDocument(request, document));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage("user not permitted to view private documents for project 0 and request 888");
    }

    @Test
    void authenticatedUserShouldBeAbleToViewPrivateDocumentsIfPermitted() {
      when(permissionsService.isAuthenticated()).thenReturn(true);
      when(document.isPrivate()).thenReturn(true);
      when(permissionsService.canAccessAllPrivateDocuments()).thenReturn(true);

      ThrowingCallable call = () -> documentAccessPolicy.validateCanViewDocument(request, document);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void authenticatedUserShouldBeAbleToViewPublicDocuments() {
      when(permissionsService.isAuthenticated()).thenReturn(true);
      when(document.isPrivate()).thenReturn(false);

      ThrowingCallable call = () -> documentAccessPolicy.validateCanViewDocument(request, document);

      assertThatCode(call).doesNotThrowAnyException();
    }

    @Test
    void unauthenticatedUsersShouldNotBeAbleToViewDocumentIfProjectStatusIsNew() {
      when(permissionsService.isAuthenticated()).thenReturn(false);
      when(project.isNew()).thenReturn(true);

      Throwable error =
          catchThrowable(() -> documentAccessPolicy.validateCanViewDocument(request, document));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage("unauthenticated users cannot view documents for projects with new status");
    }

    @Test
    void unauthenticatedUsersShouldNotBeAbleToViewPrivateDocuments() {
      when(permissionsService.isAuthenticated()).thenReturn(false);
      when(project.isNew()).thenReturn(false);
      when(document.isPrivate()).thenReturn(true);

      Throwable error =
          catchThrowable(() -> documentAccessPolicy.validateCanViewDocument(request, document));

      assertThat(error)
          .isInstanceOf(PermissionException.class)
          .hasMessage("unauthenticated users cannot view private documents");
    }

    @Test
    void unauthenticatedUsersShouldBeAbleToViewPublicDocumentsForProjectsWithoutNewStatus() {
      when(permissionsService.isAuthenticated()).thenReturn(false);
      when(project.isNew()).thenReturn(false);
      when(document.isPrivate()).thenReturn(false);

      ThrowingCallable call = () -> documentAccessPolicy.validateCanViewDocument(request, document);

      assertThatCode(call).doesNotThrowAnyException();
    }
  }
}
