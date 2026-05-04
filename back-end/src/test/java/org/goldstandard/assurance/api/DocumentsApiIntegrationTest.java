package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.goldstandard.assurance.api.populator.UserPopulator.ANY_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_REQUEST_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPDATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.DOCUMENT_UPLOAD;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.DocumentDTO;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.RequestDocument;
import org.goldstandard.assurance.api.exception.FileStoreException;
import org.goldstandard.assurance.api.fixtures.DocumentMother;
import org.goldstandard.assurance.api.populator.DocumentPopulator;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class DocumentsApiIntegrationTest extends AbstractApiTest {

  private static final byte[] FILE_DATA = new byte[] {0, 1, 2, 3, 4};

  private static final String FILE_PATH = "test-file.txt";

  private static final Integer PROJECT_ID = 1;
  private static final Integer REQUEST_ID = 2;
  private static final Integer DOCUMENT_TYPE_ID = 424242;

  private static final UUID DOCUMENT_ID = UUID.fromString("83e31c05-a2f2-49b2-befc-69fe12047efb");

  @Autowired DocumentRepository documentRepository;

  @Autowired RequestDocumentRepository requestDocumentRepository;

  @Autowired AuditRepository auditRepository;

  @Autowired ApiClient client;

  @Autowired DocumentPopulator documentPopulator;

  @Autowired JsonConverter jsonConverter;

  @Test
  void shouldNotReturnRequestDocumentsIfNotAuthenticated() {
    var response = client.getRequestDocumentsUnauthenticated(PROJECT_ID, REQUEST_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetRequestDocuments() {
    var response = client.getRequestDocuments(PROJECT_ID, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var documents = response.getBody().getContent();
    assertThat(documents).hasSize(2);

    var document = documents.getFirst();
    assertThat(document.getId()).isNotNull();
    assertThat(document.getContainerName()).isEqualTo("GS1001");
    assertThat(document.getFileName()).isEqualTo("document_" + document.getId());
    assertThat(document.getAccess()).isFalse();
    assertThat(document.getCategory()).isTrue();
    assertThat(document.getContentType()).isEqualTo("application/pdf");
    assertThat(document.getFileSize()).isEqualTo(123);
    assertThat(document.getDocumentTypeId()).isEqualTo(999999);
    assertThat(document.getDocumentCategoryType()).isEqualTo(3);
  }

  @Test
  void testGetRequestDocumentsByCategoryId() {
    var response = client.getRequestDocumentsByDisplayCategoryId(PROJECT_ID, 1, 3);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var documents = response.getBody().getContent();
    assertThat(documents).hasSize(2);

    var document = documents.getFirst();
    assertThat(document.getId()).isNotNull();
    assertThat(document.getContainerName()).isEqualTo("GS1001");
    assertThat(document.getFileName()).isEqualTo("document_" + document.getId());
    assertThat(document.getAccess()).isFalse();
    assertThat(document.getCategory()).isTrue();
    assertThat(document.getContentType()).isEqualTo("application/pdf");
    assertThat(document.getFileSize()).isEqualTo(123);
    assertThat(document.getDocumentTypeId()).isEqualTo(999999);
    assertThat(document.getDocumentCategoryType()).isEqualTo(3);
  }

  @Getter
  @AllArgsConstructor
  public enum GetRequestDocumentsCase {
    ADMIN_OMNISCIENT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, Set.of(false, true)),
    ASSIGNEE_CAN_SEE_ALL_REQUEST_DOCUMENTS(VVB_ROLE, ASSIGNED_REQUEST_USER_ID, Set.of(false, true)),
    TAC_CAN_SEE_ALL_REQUEST_DOCUMENTS(TAC_ROLE, ASSIGNED_REQUEST_USER_ID, Set.of(false, true)),
    PROJECT_OWNER_CAN_SEE_ALL_REQUEST_DOCUMENTS(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_REQUEST_USER_ID, Set.of(false, true)),
    NON_ASSIGNEE_CAN_ONLY_SEE_PUBLIC_REQUEST_DOCUMENTS(VVB_ROLE, NO_ACCOUNT_USER_ID1, Set.of(true));
    private final String role;
    private final UUID userId;
    private final Set<Boolean> expectedAccess;
  }

  @ParameterizedTest
  @EnumSource(GetRequestDocumentsCase.class)
  void testGetRequestDocuments(GetRequestDocumentsCase testCase) {
    var response =
        client.getRequestDocuments(testCase.getRole(), testCase.getUserId(), PROJECT_ID, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var documents = response.getBody().getContent();
    assertThat(documents.size()).isPositive();
    assertThat(documents)
        .map(DocumentDTO::getAccess)
        .containsExactlyInAnyOrderElementsOf(testCase.expectedAccess);
  }

  @Test
  void shouldNotDownloadDocumentIfNotAuthenticated() {
    givenFileExists();
    var documentId = documentPopulator.findPublicDocumentId();

    var response = client.downloadDocumentUnauthenticated(documentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldDownloadDocumentIfExists() {
    givenFileExists();
    var documentId = documentPopulator.findPublicDocumentId();

    var response = client.downloadDocument(ADMIN_ROLE, documentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var headers = response.getHeaders();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
    var contentDisposition = headers.getContentDisposition();
    assertThat(contentDisposition.getFilename())
        .isEqualTo(String.format("document_%s", documentId));
    assertThat(contentDisposition.getType()).isEqualTo("attachment");
    assertThat(response.getBody()).isEqualTo(FILE_DATA);
  }

  @Test
  void shouldReturnNotFoundIfDocumentDoesNotExist() {
    var notFoundId = UUID.fromString("65029732-00bd-4943-857b-ef82b52cd7ea");

    var response = client.downloadDocument(ADMIN_ROLE, notFoundId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testDownloadDocumentReturnsInternalServerErrorForDocumentNotInS3Bucket() {
    given(documentFileStore.objectExists(any()))
        .willThrow(new FileStoreException("File not found"));
    var documentId = documentPopulator.findPublicDocumentId();

    var response = client.downloadDocument(ADMIN_ROLE, documentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Getter
  @AllArgsConstructor
  public enum DownloadPrivateTestCase {
    ADMIN_OMNISCIENT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.OK),
    ASSIGNEE_CAN_SEE_ALL_REQUEST_DOCUMENTS(VVB_ROLE, ASSIGNED_REQUEST_USER_ID, HttpStatus.OK),
    PROJECT_OWNER_CAN_SEE_ALL_REQUEST_DOCUMENTS(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_REQUEST_USER_ID, HttpStatus.OK),
    NON_ASSIGNEE_CAN_ONLY_SEE_PUBLIC_REQUEST_DOCUMENTS(
        VVB_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.UNAUTHORIZED);
    private final String role;
    private final UUID userId;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(DownloadPrivateTestCase.class)
  void testDownloadPrivateDocumentAccess(DownloadPrivateTestCase testCase) {
    givenFileExists();
    var documentId = documentPopulator.findPrivateDocumentId();

    var response = client.downloadDocument(testCase.getRole(), testCase.getUserId(), documentId);

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      var headers = response.getHeaders();
      assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
      var contentDisposition = headers.getContentDisposition();
      assertThat(contentDisposition.getFilename())
          .isEqualTo(String.format("document_%s", documentId));
      assertThat(contentDisposition.getType()).isEqualTo("attachment");
      assertThat(response.getBody()).isEqualTo(FILE_DATA);
    }
  }

  @Test
  void shouldNotAllowDocumentUploadIfNotAuthenticated() {
    var response = client.uploadDocumentUnauthenticated(FILE_PATH, FILE_PATH, PROJECT_ID, 1, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testUploadDocumentIsSuccess() {
    givenFileCanBeUploaded();

    var response =
        client.uploadDocument(
            ADMIN_ROLE, ANY_USER_ID, FILE_PATH, FILE_PATH, PROJECT_ID, REQUEST_ID, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var events = auditRepository.findAllByEventSubject(DOCUMENT_UPLOAD.name());
    assertThat(events).hasSize(1);
    var additionalInfo = jsonConverter.toMap(events.getFirst().getAdditionalInformation());
    assertThat(additionalInfo)
        .contains(
            entry("projectId", PROJECT_ID),
            entry("requestId", REQUEST_ID),
            entry("documentId", response.getBody().toString()));
  }

  @Test
  void testUploadDocumentWithNotAllowedExtensionIsRejected() {
    givenFileCanBeUploaded();
    var fileName = "test-file.exe";

    var response = client.uploadDocument(ADMIN_ROLE, fileName, fileName, PROJECT_ID, 1, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUploadDocumentWithUpperCaseNotAllowedExtensionIsRejected() {
    givenFileCanBeUploaded();
    var fileName = "test-upper.EXE";

    var response = client.uploadDocument(ADMIN_ROLE, fileName, fileName, PROJECT_ID, 1, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUploadDocumentThatAlreadyExistsIsRejected() {
    given(documentFileStore.objectExists(any())).willReturn(true);

    var response = client.uploadDocument(ADMIN_ROLE, FILE_PATH, FILE_PATH, PROJECT_ID, 1, 1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Getter
  @AllArgsConstructor
  public enum UploadTestCase {
    ADMIN_OMNIPOTENT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.OK),
    ASSIGNEE_CAN_UPLOAD(VVB_ROLE, ASSIGNED_REQUEST_USER_ID, HttpStatus.OK),
    PROJECT_OWNER_CAN_UPLOAD(PROJECT_DEVELOPER_ROLE, ASSIGNED_REQUEST_USER_ID, HttpStatus.OK),
    UNASSIGNED_NON_PROJECT_OWNER_DISALLOWED(VVB_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.UNAUTHORIZED);
    private final String role;
    private final UUID userId;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(UploadTestCase.class)
  void testUploadDocumentPermissions(UploadTestCase testCase) {
    givenFileCanBeUploaded();
    var fileName = String.format("%s.pdf", testCase.name());

    var response =
        client.uploadDocument(
            testCase.getRole(),
            testCase.getUserId(),
            FILE_PATH,
            fileName,
            PROJECT_ID,
            REQUEST_ID,
            1);

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      assertThat(response.getBody()).isNotNull();
      var events = findAuditEvents(DOCUMENT_UPLOAD, response.getBody().toString());
      assertThat(events).hasSize(1);
      var additionalInfo = jsonConverter.toMap(events.getFirst().getAdditionalInformation());
      assertThat(additionalInfo).containsEntry("projectId", 1);
      assertThat(additionalInfo).containsEntry("requestId", REQUEST_ID);
    }
  }

  @Test
  void shouldNotAllowDocumentUpdateIfNotAuthenticated() {
    var documentToUpdate = documentRepository.findAll().iterator().next();
    var update = new DocumentUpdate(true, true, DOCUMENT_TYPE_ID, true);

    var response = client.updateDocumentUnauthenticated(documentToUpdate.getId(), update);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testUpdateDocument() {
    var documentToUpdate = documentRepository.findAll().iterator().next();
    try {
      var update = new DocumentUpdate(true, true, DOCUMENT_TYPE_ID, true);

      var response = client.updateDocument(ADMIN_ROLE, documentToUpdate.getId(), update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();

      var document = response.getBody();
      assertThat(document.getId()).isEqualTo(documentToUpdate.getId());
      assertThat(document.getAccess()).isEqualTo(update.access());
      assertThat(document.getCategory()).isEqualTo(update.category());
      assertThat(document.getDocumentTypeId()).isEqualTo(update.documentTypeId());
      assertThat(document.getIsLatest()).isEqualTo(update.isLatest());
      assertThat(document.getDocumentCategoryType()).isNotNull();

      var auditEvents = findAuditEvents(DOCUMENT_UPDATE, documentToUpdate.getId().toString());
      assertThat(auditEvents).hasSize(1);
      var additionalInfo = jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation());
      assertThat(additionalInfo).containsEntry("projectId", PROJECT_ID);
      assertThat(additionalInfo).containsEntry("requestId", 1);
    } finally {
      documentRepository.save(documentToUpdate);
    }
  }

  @ParameterizedTest
  @EnumSource(UploadTestCase.class)
  void testUpdateDocumentPermissions(UploadTestCase testCase) {
    var testDocument = createTestRequestDocument();
    var update = new DocumentUpdate(false, true, DOCUMENT_TYPE_ID, true);

    var response =
        client.updateDocument(
            testCase.getRole(), testCase.getUserId(), testDocument.getId(), update);

    assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      assertThat(response.getBody()).isNotNull();
      var document = response.getBody();
      assertThat(document.getId()).isEqualTo(testDocument.getId());
      assertThat(document.getAccess()).isEqualTo(update.access());
      assertThat(document.getCategory()).isEqualTo(update.category());
      assertThat(document.getDocumentTypeId()).isEqualTo(update.documentTypeId());
      assertThat(document.getIsLatest()).isEqualTo(update.isLatest());

      var auditEvents = findAuditEvents(DOCUMENT_UPDATE, testDocument.getId().toString());
      assertThat(auditEvents).hasSize(1);
      var additionalInfo = jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation());
      assertThat(additionalInfo).containsEntry("projectId", PROJECT_ID);
      assertThat(additionalInfo).containsEntry("requestId", REQUEST_ID);
    }
  }

  @Test
  void shouldNotAllowRequestDocumentDeleteIfNotAuthenticated() {
    var response = client.deleteRequestDocumentUnauthenticated(REQUEST_ID, DOCUMENT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldNotAllowRequestDocumentDeleteIfNotAdmin() {
    var response = client.deleteRequestDocument(PROJECT_DEVELOPER_ROLE, REQUEST_ID, DOCUMENT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void shouldAllowRequestDocumentDeleteAsAdmin() {
    var response = client.deleteRequestDocument(ADMIN_ROLE, REQUEST_ID, DOCUMENT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private void givenFileExists() {
    given(documentFileStore.objectExists(any())).willReturn(true);
    given(documentFileStore.getObjectAsStream(any()))
        .willReturn(new ByteArrayInputStream(FILE_DATA));
  }

  private void givenFileCanBeUploaded() {
    given(documentFileStore.objectExists(any())).willReturn(false);
    given(documentFileStore.uploadStream(any(), any(), any())).willReturn("EXPECTED_HASH");
  }

  private Document createTestRequestDocument() {
    var documentId = UUID.randomUUID();
    var document = DocumentMother.document(documentId, DOCUMENT_TYPE_ID);
    document = documentRepository.save(document);
    var requestDocument = new RequestDocument();
    requestDocument.setDocumentId(documentId);
    requestDocument.setRequestId(REQUEST_ID);
    requestDocumentRepository.add(requestDocument);
    return document;
  }

  private List<AuditEvent> findAuditEvents(AuditEventSubject subject, String documentId) {
    return findAuditEvents(
        subject,
        e ->
            jsonConverter.toMap(e.getAdditionalInformation()).get("documentId").equals(documentId));
  }

  private List<AuditEvent> findAuditEvents(
      AuditEventSubject subject, Predicate<AuditEvent> predicate) {
    return auditRepository.findAllByEventSubject(subject.name()).stream()
        .filter(predicate)
        .toList();
  }
}
