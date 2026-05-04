package org.goldstandard.assurance.api.service.document;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentUpdateCalculator {

  private final CategorisedRequestDocumentViewRepository repository;

  public Collection<Document> calculateDocumentsToMakePrivate(
      Document originalDocument, Document updatedDocument, Integer requestId) {
    if (!shouldUpdateOtherDocuments(originalDocument, updatedDocument)) {
      return Collections.emptyList();
    }
    return getOtherPublicRequestDocumentsOfType(requestId, updatedDocument.getDocumentTypeId())
        .stream()
        .map(Document::makePrivate)
        .toList();
  }

  private boolean shouldUpdateOtherDocuments(Document originalDocument, Document updatedDocument) {
    return !updatedDocument.hasOtherType() && hasBeenMadePublic(originalDocument, updatedDocument);
  }

  private boolean hasBeenMadePublic(Document originalDocument, Document updatedDocument) {
    return originalDocument.isPrivate() && updatedDocument.isPublic();
  }

  private Collection<Document> getOtherPublicRequestDocumentsOfType(
      Integer requestId, Integer documentTypeId) {
    return repository.findAllPublicRequestDocumentsWithType(requestId, documentTypeId);
  }
}
