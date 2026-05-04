package org.goldstandard.assurance.api.service.document;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentUpdateValidator {

  private final PermissionsService permissionsService;

  public void validateUpdateAllowed(Document document, DocumentUpdate update) {
    validateUpdateAccessAllowed(document, update);
  }

  public void validateUpdateAccessAllowed(Document document, DocumentUpdate update) {
    if (!isAccessChanged(update, document)) {
      return;
    }

    if (document.uploadedByReviewer()) {
      throw new PermissionException(
          "Updating document visibility not permitted for documents uploaded by reviewers");
    }

    if (permissionsService.isReviewerOrVVBOrRegistry()) {
      throw new PermissionException("Updating document visibility not permitted for user");
    }
  }

  private boolean isAccessChanged(DocumentUpdate update, Document document) {
    return !Objects.equals(update.access(), document.getAccess());
  }
}
