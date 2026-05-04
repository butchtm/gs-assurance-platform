package org.goldstandard.assurance.api.service.document;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.AssignmentPermissionsService;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Builder
@Component
@Slf4j
public class DocumentAccessPolicy {

  private final ProjectFinder projectFinder;
  private final PermissionsService permissionsService;
  private final AssignmentPermissionsService assignmentPermissionsService;
  private final DocumentTypeUploadValidator documentTypeValidator;
  private final RequestFinder requestFinder;
  private final BatchRequestFinder batchRequestFinder;

  public void validateUserCanManageDocuments(Project project, Request request, int documentTypeId) {
    validateUserCanManageDocumentsForProject(project);
    validateUserCanManageDocumentsForRequest(request);
    documentTypeValidator.validateUserCanUpload(documentTypeId);
  }

  public boolean canViewPrivateDocuments(Integer projectId, Integer requestId) {
    var project = projectFinder.forceGetProjectById(projectId);
    var request = requestFinder.getRequestById(requestId);
    return canViewPrivateDocuments(project, request);
  }

  public boolean canUserViewReviewerUploadedDocuments(Integer requestId) {
    if (permissionsService.isAdminOrArm()) {
      return true;
    }
    if (permissionsService.isReviewer()) {
      return assignmentPermissionsService.isRequestAssignedToUserAccount(requestId);
    }
    return false;
  }

  public void validateCanViewDocument(Request request, Document document) {
    var project = request.getProject();
    if (permissionsService.isAuthenticated()) {
      validateAuthenticatedUserCanViewDocument(request, project, document);
      return;
    }
    validateUnauthenticatedUserCanViewDocument(project, document);
  }

  private boolean canViewPrivateDocuments(Project project, Request request) {
    if (permissionsService.canAccessAllPrivateDocuments()) {
      return true;
    }
    if (isProjectOwner(project)) {
      return true;
    }
    if (isReviewerAssignedToProjectRequest(project)) {
      return true;
    }
    if (assignmentPermissionsService.isRequestAssignedToUserAccount(request.getId())) {
      return true;
    }
    return isBatchParentAndReviewerAssignedToChildProject(request);
  }

  private void validateUserCanManageDocumentsForProject(Project project) {
    if (!canManageDocuments(project)) {
      throw new PermissionException(
          "user not permitted to manage documents for project %s", project.getId());
    }
  }

  private void validateAuthenticatedUserCanViewDocument(
      Request request, Project project, Document document) {
    if (document.isPrivate() && !canViewPrivateDocuments(project, request)) {
      throw new PermissionException(
          "user not permitted to view private documents for project %d and request %d",
          project.getId(), request.getId());
    }
  }

  private void validateUnauthenticatedUserCanViewDocument(Project project, Document document) {
    if (project.isNew()) {
      throw new PermissionException(
          "unauthenticated users cannot view documents for projects with new status");
    }
    if (document.isPrivate()) {
      throw new PermissionException("unauthenticated users cannot view private documents");
    }
  }

  private void validateUserCanManageDocumentsForRequest(Request request) {
    if (permissionsService.isAdminOrArm()) {
      return;
    }
    if (request.hasInactiveCurrentStatus()) {
      throw new PermissionException("user not permitted to manage documents for inactive requests");
    }
  }

  private boolean canManageDocuments(Project project) {
    return permissionsService.isAdminOrFinanceOrArm()
        || isProjectOwner(project)
        || isReviewerAssignedToProjectRequest(project);
  }

  private boolean isProjectOwner(Project project) {
    if (permissionsService.isProjectOwner(project)) {
      log.debug("current users owns project {}", project.getId());
      return true;
    }
    return false;
  }

  private boolean isBatchParentAndReviewerAssignedToChildProject(Request request) {
    if (!request.isBatchParent()) {
      return false;
    }
    return batchRequestFinder
        .getChildProjects(request)
        .anyMatch(this::isReviewerAssignedToProjectRequest);
  }

  private boolean isReviewerAssignedToProjectRequest(Project project) {
    if (!permissionsService.isReviewerOrVVBOrRegistry()) {
      return false;
    }
    if (!assignmentPermissionsService.hasActiveRequestsAssignedToUserAccount(project)) {
      return false;
    }
    log.debug("current user is reviewer or VVB and is has request assigned to project account");
    return true;
  }
}
