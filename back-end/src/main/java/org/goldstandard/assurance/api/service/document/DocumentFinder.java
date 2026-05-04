package org.goldstandard.assurance.api.service.document;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.goldstandard.assurance.api.repository.DocumentRepository;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentFinder {
  private final DocumentRepository documentRepository;
  private final CategorisedRequestDocumentViewRepository requestDocumentViewRepository;
  private final CategorisedRequestDocumentViewSpecFactory specFactory;
  private final RequestFinder requestFinder;
  private final DocumentAccessPolicy documentAccessPolicy;

  public Page<CategorisedRequestDocumentView> getRequestDocumentsPaged(
      Integer projectId, Integer requestId, Integer displayCategoryId, Pageable pageable) {
    var lookupRequestId = getLookupRequestId(projectId, requestId);
    var spec = specFactory.toSpec(projectId, lookupRequestId, displayCategoryId);
    return requestDocumentViewRepository.findAll(spec, pageable);
  }

  public List<CategorisedRequestDocumentView> getRequestDocuments(
      Integer projectId, Integer requestId, Integer displayCategoryId) {
    var lookupRequestId = getLookupRequestId(projectId, requestId);
    var spec = specFactory.toSpec(projectId, lookupRequestId, displayCategoryId);
    return requestDocumentViewRepository.findAll(spec);
  }

  public Page<CategorisedRequestDocumentView> getRequestDocuments(
      Integer projectId, Integer requestId, Pageable pageable) {
    var lookupRequestId = getLookupRequestId(projectId, requestId);
    var spec = specFactory.toSpec(projectId, lookupRequestId);
    return requestDocumentViewRepository.findAll(spec, pageable);
  }

  public Document getDocument(UUID documentId) {
    return documentRepository.forceFindById(documentId);
  }

  public List<Document> getDocuments(Collection<UUID> documentIds) {
    return IterableUtils.toList(documentRepository.findAllById(documentIds));
  }

  public Document getDocumentIfAllowedToView(UUID documentId) {
    var view =
        requestDocumentViewRepository
            .findOneByDocumentId(documentId)
            .orElseThrow(() -> new NotFoundException("document not found with id", documentId));
    var request = view.getRequest();
    var document = view.getDocument();
    documentAccessPolicy.validateCanViewDocument(request, document);
    return document;
  }

  private Integer getLookupRequestId(Integer projectId, Integer requestId) {
    var batchIds = requestFinder.getBatchParentIdView(projectId, requestId);
    return batchIds.getParentRequestIdOrRequestId();
  }
}
