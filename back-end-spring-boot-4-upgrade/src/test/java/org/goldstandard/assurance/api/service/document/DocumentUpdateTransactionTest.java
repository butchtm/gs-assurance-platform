package org.goldstandard.assurance.api.service.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPDATE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentUpdateTransactionTest {

  private static final int REQUEST_ID = 2;
  private static final int PROJECT_ID = 3;

  @InjectMocks DocumentUpdateTransaction transaction;

  @Mock DocumentRepository repository;

  @Mock AuditService auditService;

  @Mock DocumentAuditInfoFactory auditInfoFactory;

  @Captor ArgumentCaptor<Map<String, Object>> auditInfoCaptor;

  @Captor ArgumentCaptor<List<Document>> otherDocumentCaptor;

  @Test
  void shouldSaveDocument() {
    var document = givenDocumentWithId();
    var expectedDocument = givenSavedDocument(document);

    var result = transaction.performUpdate(document, REQUEST_ID, PROJECT_ID);

    assertThat(result).isEqualTo(expectedDocument);
  }

  @Test
  void shouldAuditUpdate() {
    var document = givenDocumentWithId();
    var savedDocument = givenSavedDocument(document);
    var expectedAuditInfo = givenAuditInfo(savedDocument);

    transaction.performUpdate(document, REQUEST_ID, PROJECT_ID);

    verify(auditService).auditEvent(eq(DOCUMENT_UPDATE), auditInfoCaptor.capture());
    Map<String, Object> auditInfo = auditInfoCaptor.getValue();
    assertThat(auditInfo).isEqualTo(expectedAuditInfo);
  }

  @Test
  void shouldSaveOtherDocumentsIfProvided() {
    var document = givenDocumentWithId();
    givenSavedDocument(document);
    var otherDocuments = List.of(mock(Document.class));

    transaction.performUpdate(document, REQUEST_ID, PROJECT_ID, otherDocuments);

    verify(repository).saveAll(otherDocumentCaptor.capture());
    assertThat(otherDocumentCaptor.getValue()).isEqualTo(otherDocuments);
  }

  private Document givenSavedDocument(Document document) {
    var savedDocument = givenDocumentWithId();
    when(repository.save(document)).thenReturn(savedDocument);
    return savedDocument;
  }

  private Map<String, Object> givenAuditInfo(Document document) {
    Map<String, Object> auditInfo = new HashMap<>();
    when(auditInfoFactory.build(PROJECT_ID, REQUEST_ID, document.getId(), Collections.emptyList()))
        .thenReturn(auditInfo);
    return auditInfo;
  }

  private static Document givenDocumentWithId() {
    return Document.builder().id(UUID.fromString("d1f1e40e-ce5a-4b39-b35c-d7e1532f1f9b")).build();
  }
}
