package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.DocumentConverter;
import org.goldstandard.assurance.api.dto.DocumentDTO;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.service.document.DocumentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DocumentController {
  private final DocumentService service;
  private final DocumentDownloadResponseFactory downloadResponseFactory;
  private final DocumentConverter mapper;

  @GetMapping("/projects/{projectId}/requests/{requestId}/documents")
  public ResponseEntity<PagedResource<DocumentDTO>> getDocumentsForRequest(
      @PageableDefault(
              sort = {"request_id"},
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable,
      @PathVariable Integer projectId,
      @PathVariable Integer requestId) {
    var documents = service.getRequestDocuments(projectId, requestId, pageable).map(mapper::toDto);
    return ResponseEntity.ok(new PagedResource<>(documents));
  }

  @GetMapping(value = "/projects/{projectId}/requests/{requestId}/documents/{displayCategoryId}")
  public ResponseEntity<PagedResource<DocumentDTO>> getDocumentsForRequestByCategory(
      @PageableDefault(
              sort = {"document_uploadedTimestamp"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable,
      @PathVariable Integer projectId,
      @PathVariable Integer requestId,
      @PathVariable Integer displayCategoryId) {
    var documents =
        service
            .getRequestDocumentsPaged(projectId, requestId, displayCategoryId, pageable)
            .map(mapper::toDto);

    return ResponseEntity.ok(new PagedResource<>(documents));
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE,
    TAC_ROLE
  })
  @GetMapping(
      value = "/projects/{projectId}/requests/{requestId}/documents/{displayCategoryId}/download",
      produces = "application/zip")
  public ResponseEntity<StreamingResponseBody> downloadDocumentsForRequest(
      @PathVariable Integer projectId,
      @PathVariable Integer requestId,
      @PathVariable Integer displayCategoryId) {
    var documents =
        service.getRequestDocuments(projectId, requestId, displayCategoryId).stream()
            .map(CategorisedRequestDocumentView::getDocument)
            .toList();
    return downloadResponseFactory.toStreamResponse(requestId, documents, displayCategoryId);
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE,
    GS_REVIEWER_ROLE
  })
  @PostMapping(
      "/projects/{projectId}/requests/{requestId}/document-types/{documentTypeId}/documents")
  public ResponseEntity<UUID> uploadDocument(
      @PathVariable Integer projectId,
      @PathVariable Integer requestId,
      @PathVariable Integer documentTypeId,
      @RequestParam("file") MultipartFile file) {
    var id = service.uploadDocument(projectId, requestId, documentTypeId, file);
    return ResponseEntity.ok(id);
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REVIEWER_ROLE,
    GS_REGISTRY_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE
  })
  @PutMapping("/documents/{documentId}")
  public ResponseEntity<Document> updateDocument(
      @PathVariable UUID documentId, @Valid @RequestBody DocumentUpdate update) {
    var document = service.updateDocument(documentId, update);
    return ResponseEntity.ok(document);
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE,
    TAC_ROLE
  })
  @GetMapping("/documents/{documentId}/download")
  public ResponseEntity<Void> downloadDocument(
      @PathVariable UUID documentId, HttpServletResponse response) {
    var document = service.getDocument(documentId);
    downloadResponseFactory.addDocumentToResponse(document, response);
    return ResponseEntity.ok().build();
  }

  @RolesAllowed({ADMIN_ROLE})
  @DeleteMapping("/requests/{requestId}/documents/{documentId}")
  public ResponseEntity<Void> deleteDocumentFromRequest(
      @PathVariable Integer requestId, @PathVariable UUID documentId) {
    service.deleteRequestDocument(requestId, documentId);
    return ResponseEntity.noContent().build();
  }
}
