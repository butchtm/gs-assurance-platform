package org.goldstandard.assurance.api.service.document;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Request;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class DocumentUploader {
  private final DocumentFactory documentFactory;
  private final DocumentStoreService documentStoreService;
  private final DocumentUploadTransaction documentUploadTransaction;

  public UUID upload(Request request, int documentTypeId, MultipartFile file) {
    var document = uploadAndSaveDocument(request, documentTypeId, file);
    return document.getId();
  }

  private Document uploadAndSaveDocument(Request request, int documentTypeId, MultipartFile file) {
    var hash = documentStoreService.uploadFile(request.getProject().getGsid(), file);
    var newDocument = documentFactory.createDocument(file, request, documentTypeId, hash);
    return documentUploadTransaction.perform(newDocument, request);
  }
}
