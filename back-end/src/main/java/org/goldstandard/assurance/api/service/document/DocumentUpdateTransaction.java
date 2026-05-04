package org.goldstandard.assurance.api.service.document;

import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPDATE;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentUpdateTransaction {

  private final DocumentRepository repository;
  private final AuditService auditService;
  private final DocumentAuditInfoFactory auditInfoFactory;

  public Document performUpdate(Document document, int requestId, int projectId) {
    return performUpdate(document, requestId, projectId, Collections.emptyList());
  }

  @Transactional
  public Document performUpdate(
      Document document, int requestId, int projectId, Collection<Document> otherDocuments) {
    var savedDocument = repository.save(document);
    repository.saveAll(otherDocuments);
    var auditInfo =
        auditInfoFactory.build(projectId, requestId, savedDocument.getId(), toIds(otherDocuments));
    auditService.auditEvent(DOCUMENT_UPDATE, auditInfo);
    return savedDocument.withDocumentCategoryType(document.getDocumentCategoryType());
  }

  private static Collection<UUID> toIds(Collection<Document> otherDocuments) {
    return otherDocuments.stream().map(Document::getId).toList();
  }
}
