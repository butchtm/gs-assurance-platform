package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentDTO;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocumentConverter {

  private final AccountTypeConverter mapper;

  public DocumentDTO toDto(CategorisedRequestDocumentView view) {
    var document = view.getDocument();
    return DocumentDTO.builder()
        .id(document.getId())
        .containerName(document.getContainerName())
        .fileName(document.getFileName())
        .fileSize(document.getFileSize())
        .hashValue(document.getHashValue())
        .access(document.getAccess())
        .category(document.getCategory())
        .documentTypeId(document.getDocumentTypeId())
        .contentType(document.getContentType())
        .documentHash(document.getDocumentHash())
        .uploadedTimestamp(document.getUploadedTimestamp())
        .modifiedTimestamp(document.getModifiedTimestamp())
        .isLatest(document.getIsLatest())
        .isMissing(document.getIsMissing())
        .documentCategoryType(view.getDocumentDisplayCategoryId())
        .piiScanStatus(view.getPiiScanStatusCode())
        .piiAction(view.getPiiActionCode())
        .uploadedByAccountType(document.getUploadedByAccountType().map(mapper::toDto).orElse(null))
        .build();
  }
}
