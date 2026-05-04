package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.RequestPopulator.TEST_REQUESTS;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.RequestDocument;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DocumentPopulator {
  public static final int NUMBER_OF_DOCUMENTS_PER_REQUEST = 2;

  /** We need a pool of static UUIDs so we can reference them in tests and JSON files. */
  public static final List<UUID> UUIDS_TO_USE =
      List.of(
          UUID.fromString("63d3915f-088e-3eed-8f9d-2e581aeed325"),
          UUID.fromString("7d11f7c8-ce66-3613-be1e-130167c28c31"),
          UUID.fromString("e10030a4-492e-3009-93d4-ed2c297092e5"),
          UUID.fromString("e550d31d-bb9c-35f0-8ce3-fa87c8e79746"),
          UUID.fromString("12d22ecd-a412-4f92-bd29-22dc2cd1d947"),
          UUID.fromString("1757591f-4c9f-484b-956a-90bd2b9679c0"),
          UUID.fromString("e938ea19-f2a4-4133-b20b-007b56ea3f76"),
          UUID.fromString("43c209d6-8ce0-476f-981e-d298406a335a"),
          UUID.fromString("72d4349c-ac1d-46fb-abd5-5f054617455"),
          UUID.fromString("e5ba46f4-7ee3-40d8-9741-aa77d78768c3"),
          UUID.fromString("65375059-1c87-4432-a7ff-6bc030abb56e"),
          UUID.fromString("cd91559b-1d37-43f5-9169-286ad970f250"));

  public static ZonedDateTime TEST_UPLOADED_TIMESTAMP =
      ZonedDateTime.of(2023, 12, 5, 16, 11, 0, 0, ZoneId.of("Asia/Kolkata"));

  public static ZonedDateTime TEST_MODIFIED_TIMESTAMP = TEST_UPLOADED_TIMESTAMP.plusDays(1);

  public static List<Document> TEST_DOCUMENTS;

  @Autowired private RequestDocumentRepository requestDocumentRepository;

  @Autowired private DocumentRepository documentRepository;

  @Autowired
  private CategorisedRequestDocumentViewRepository categorisedRequestDocumentViewRepository;

  public UUID findPublicDocumentId() {
    return findDocumentId(Document::isPublic);
  }

  public UUID findPrivateDocumentId() {
    return findDocumentId(Document::isPrivate);
  }

  public UUID findDocumentId(Predicate<Document> predicate) {
    return TEST_DOCUMENTS.stream().filter(predicate).findFirst().orElseThrow().getId();
  }

  public UUID findPublicDocumentForProjectWithNewStatus() {
    return TEST_DOCUMENTS.get(5).getId();
  }

  /**
   * We want to create a number of documents split equally between two different requests and each
   * list of documents should have half as public and half as private controlled by the access
   * parameter.
   */
  public void populate() {
    documentRepository.deleteAllBaseProjectDocument();
    var documents = new ArrayList<Document>();
    var requestDocuments = new ArrayList<RequestDocument>();
    var uuidIterator = UUIDS_TO_USE.iterator();
    for (var request : TEST_REQUESTS) {
      for (int i = 1; i <= NUMBER_OF_DOCUMENTS_PER_REQUEST; i++) {
        var requestDocument = new RequestDocument();
        requestDocument.setRequestId(request.getId());
        requestDocument.setDocumentId(uuidIterator.next());

        var builder =
            Document.builder()
                .id(requestDocument.getDocumentId())
                .containerName(request.getProject().getGsid())
                .contentType("application/pdf")
                .fileName("document_" + requestDocument.getDocumentId())
                .fileSize(123L)
                .access(i % 2 == 0)
                .category(true)
                .documentType(new DocumentType(999999))
                .documentCategoryType(3)
                .uploadedTimestamp(TEST_UPLOADED_TIMESTAMP);
        if (0 == request.getId() % 2) {
          builder.modifiedTimestamp(TEST_MODIFIED_TIMESTAMP);
        }
        var document = builder.build();
        requestDocument.setDocument(document);

        documents.add(document);
        requestDocuments.add(requestDocument);
        var savedDocument = documentRepository.save(document);
        var loadedDocument = documentRepository.forceFindById(savedDocument.getId());
        var project = request.getProject();
        log.info(
            "populated document project {} {} and request {} with access {} and filename {}",
            project.getId(),
            project.getGsid(),
            request.getId(),
            loadedDocument.getAccess(),
            loadedDocument.getFileName());
        documentRepository.saveBaseProjectDocument(project.getId(), loadedDocument.getId());
      }
    }
    requestDocuments.forEach(rd -> requestDocumentRepository.add(rd));
    TEST_DOCUMENTS = documents;
  }

  public boolean isPopulated() {
    return requestDocumentRepository.count() > 0;
  }
}
