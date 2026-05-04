package org.goldstandard.assurance.api.service.document;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.service.RequestTypeDocumentTypeService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class DocumentFactory {

  private final RequestTypeDocumentTypeService service;
  private final Clock clock;
  private final Supplier<UUID> uuidSupplier;
  private final AuthenticatedUserSupplier userSupplier;

  public Document createDocument(
      MultipartFile file, Request request, int documentTypeId, String hash) {
    var now = ZonedDateTime.now(clock);
    return Document.builder()
        .documentHash(hash)
        .id(uuidSupplier.get())
        .access(false)
        .containerName(request.getProject().getGsid())
        .documentType(new DocumentType(documentTypeId))
        .fileName(file.getOriginalFilename())
        .contentType(file.getContentType())
        .fileSize(file.getSize())
        .modifiedTimestamp(now)
        .uploadedTimestamp(now)
        .uploadedBy(userSupplier.forceGetAuthenticatedUser())
        .isLatest(true)
        .category(false)
        .documentCategoryType(findDocumentCategoryTypeId(request, documentTypeId))
        .build();
  }

  private int findDocumentCategoryTypeId(Request request, int documentTypeId) {
    int typeId = request.getRequestType().getId();
    return service.findDocumentDisplayCategoryId(typeId, documentTypeId);
  }
}
