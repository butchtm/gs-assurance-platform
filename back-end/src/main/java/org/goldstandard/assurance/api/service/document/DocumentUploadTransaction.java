package org.goldstandard.assurance.api.service.document;

import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPLOAD;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentUploadTransaction {

  private final DocumentRepository documentRepository;
  private final RequestDocumentRepository requestDocumentRepository;
  private final AuditService auditService;
  private final DocumentAuditInfoFactory auditInfoFactory;

  @Transactional
  public Document perform(Document document, Request request) {
    Document savedDocument = documentRepository.save(document);
    var documentId = savedDocument.getId();
    var requestId = request.getId();
    var projectId = request.getProject().getId();
    requestDocumentRepository.add(requestId, documentId);
    documentRepository.saveBaseProjectDocument(projectId, documentId);
    auditService.auditEvent(
        DOCUMENT_UPLOAD, auditInfoFactory.build(projectId, requestId, documentId));
    return savedDocument;
  }
}
