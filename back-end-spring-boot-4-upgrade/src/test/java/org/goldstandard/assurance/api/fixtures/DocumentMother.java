package org.goldstandard.assurance.api.fixtures;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentMother {

  public static Document document(UUID documentId, int documentTypeId) {
    return buildDocument(documentId, documentTypeId).build();
  }

  public static Document withContentType(UUID documentId, String contentType) {
    return buildDocument(documentId, 1).contentType(contentType).build();
  }

  private static Document.DocumentBuilder buildDocument(UUID documentId, int documentTypeId) {
    return Document.builder()
        .id(documentId)
        .containerName("TEST")
        .fileName(documentId.toString())
        .fileSize(42L)
        .access(false)
        .category(true)
        .documentType(new DocumentType(documentTypeId))
        .contentType("application/octet-stream")
        .uploadedTimestamp(ZonedDateTime.now())
        .modifiedTimestamp(ZonedDateTime.now());
  }
}
