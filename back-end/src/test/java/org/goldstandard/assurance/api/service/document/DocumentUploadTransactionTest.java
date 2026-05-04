package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPLOAD;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUploadTransactionTest {

  @InjectMocks DocumentUploadTransaction transaction;

  @Mock DocumentRepository documentRepository;

  @Mock RequestDocumentRepository requestDocumentRepository;

  @Mock AuditService auditService;

  @Mock DocumentAuditInfoFactory auditInfoFactory;

  @Captor ArgumentCaptor<Map<String, Object>> auditInfoCaptor;

  @Test
  void shouldSaveDocumentToRequestAndProject() {
    var project = givenProject();
    var request = givenRequestForProject(project);
    var document = mock(Document.class);
    var savedDocument = givenSavedDocument(document);

    transaction.perform(document, request);

    verify(requestDocumentRepository).add(request.getId(), savedDocument.getId());
    verify(documentRepository).saveBaseProjectDocument(project.getId(), savedDocument.getId());
  }

  @Test
  void shouldAuditDocumentUpload() {
    var project = givenProject();
    var request = givenRequestForProject(project);
    var document = mock(Document.class);
    var savedDocument = givenSavedDocument(document);
    var expectedAuditInfo = givenAuditInfo(project, request, savedDocument);

    transaction.perform(document, request);

    verify(auditService).auditEvent(eq(DOCUMENT_UPLOAD), auditInfoCaptor.capture());
    Map<String, Object> auditInfo = auditInfoCaptor.getValue();
    assertThat(auditInfo).isEqualTo(expectedAuditInfo);
  }

  private Document givenSavedDocument(Document document) {
    var savedDocument = givenDocumentWithId();
    when(documentRepository.save(document)).thenReturn(savedDocument);
    return savedDocument;
  }

  private Map<String, Object> givenAuditInfo(Project project, Request request, Document document) {
    Map<String, Object> auditInfo = new HashMap<>();
    when(auditInfoFactory.build(project.getId(), request.getId(), document.getId()))
        .thenReturn(auditInfo);
    return auditInfo;
  }

  private static Project givenProject() {
    var project = new Project();
    project.setId(1);
    return project;
  }

  private static Request givenRequestForProject(Project project) {
    var request = new Request();
    request.setId(2);
    request.setProject(project);
    return request;
  }

  private static Document givenDocumentWithId() {
    return Document.builder().id(UUID.fromString("da3ad6ae-c928-4313-baf9-2100fbee7044")).build();
  }
}
