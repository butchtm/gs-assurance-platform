package org.goldstandard.assurance.api.service.document;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentUpdater {

  private final DocumentRepository repository;
  private final RequestFinder requestFinder;
  private final DocumentAccessPolicy accessPolicy;
  private final DocumentUpdateValidator validator;
  private final DisplayCategoryIdFinder categoryIdFinder;
  private final DocumentUpdateApplier applier;
  private final DocumentUpdateTransaction transaction;
  private final DocumentUpdateCalculator updateCalculator;

  public Document updateDocument(UUID documentId, DocumentUpdate update) {
    var originalDocument = repository.forceFindById(documentId);
    var requestId = requestFinder.getRequestIdAssociatedToDocument(documentId);
    var request = requestFinder.getRequestById(requestId);
    var project = request.getProject();
    accessPolicy.validateUserCanManageDocuments(project, request, update.documentTypeId());
    validator.validateUpdateAllowed(originalDocument, update);
    var categoryId = categoryIdFinder.getDisplayCategoryId(documentId, requestId);

    var updatedDocument = applier.applyUpdates(originalDocument, update, categoryId);
    var otherDocuments =
        updateCalculator.calculateDocumentsToMakePrivate(
            originalDocument, updatedDocument, requestId);
    return transaction.performUpdate(updatedDocument, requestId, project.getId(), otherDocuments);
  }
}
