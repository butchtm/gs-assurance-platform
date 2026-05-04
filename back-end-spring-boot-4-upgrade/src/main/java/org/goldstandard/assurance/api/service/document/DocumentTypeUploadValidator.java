package org.goldstandard.assurance.api.service.document;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.repository.DocumentTypeRepository;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentTypeUploadValidator {

  private final PermissionsService permissionsService;
  private final DocumentTypeRepository repository;

  public void validateUserCanUpload(int documentTypeId) {
    if (permissionsService.isReviewer()) {
      validateReviewCanUpload(documentTypeId);
    }
  }

  private void validateReviewCanUpload(int documentTypeId) {
    var documentType = repository.forceFindById(documentTypeId);
    if (documentType.isReviewFeedbackForm()) {
      return;
    }
    throw new PermissionException(
        "reviewer not permitted to upload %s documents", documentType.getDescription());
  }
}
