package org.goldstandard.assurance.api.service.document;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.goldstandard.assurance.api.repository.RequestDocumentRepository;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {
  private final DocumentUploader documentUploader;
  private final DocumentUpdater documentUpdater;
  private final ProjectFinder projectFinder;
  private final RequestFinder requestFinder;
  private final DocumentAccessPolicy documentAccessPolicy;
  private final DocumentFinder documentFinder;
  private final RequestDocumentRepository requestDocumentRepository;
  private final FileExtensionValidator extensionValidator;
  private final DocumentPiiAnalysisService piiAnalysisService;

  public UUID uploadDocument(int projectId, int requestId, int documentTypeId, MultipartFile file) {
    extensionValidator.validateFileExtension(file);
    var request = requestFinder.getRequestById(requestId);
    if (request.isBatchChild()) {
      throw new BadRequestException("Documents cannot be uploaded to child batch requests");
    }

    var project = projectFinder.forceGetProjectById(projectId);
    request.validateBelongsToProject(projectId);
    documentAccessPolicy.validateUserCanManageDocuments(project, request, documentTypeId);

    var id = documentUploader.upload(request, documentTypeId, file);
    piiAnalysisService.startAnalysis(id);
    return id;
  }

  public Document updateDocument(UUID documentId, DocumentUpdate update) {
    return documentUpdater.updateDocument(documentId, update);
  }

  public Page<CategorisedRequestDocumentView> getRequestDocumentsPaged(
      Integer projectId, Integer requestId, Integer displayCategoryId, Pageable pageable) {
    return documentFinder.getRequestDocumentsPaged(
        projectId, requestId, displayCategoryId, pageable);
  }

  public List<CategorisedRequestDocumentView> getRequestDocuments(
      Integer projectId, Integer requestId, Integer displayCategoryId) {
    return documentFinder.getRequestDocuments(projectId, requestId, displayCategoryId);
  }

  public Page<CategorisedRequestDocumentView> getRequestDocuments(
      Integer projectId, Integer requestId, Pageable pageable) {
    return documentFinder.getRequestDocuments(projectId, requestId, pageable);
  }

  public Document getDocument(UUID documentId) {
    return documentFinder.getDocumentIfAllowedToView(documentId);
  }

  public Document forceGetDocument(UUID documentId) {
    return documentFinder.getDocument(documentId);
  }

  public void deleteRequestDocument(Integer requestId, UUID documentId) {
    requestDocumentRepository.deleteByRequestIdAndDocumentId(requestId, documentId);
  }
}
