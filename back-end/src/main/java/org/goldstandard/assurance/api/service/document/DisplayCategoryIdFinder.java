package org.goldstandard.assurance.api.service.document;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisplayCategoryIdFinder {

  private final CategorisedRequestDocumentViewRepository repository;

  public Integer getDisplayCategoryId(UUID documentId, int requestId) {
    return repository.findAllByRequestId(requestId).stream()
        .filter(d -> d.getDocument().getId().equals(documentId))
        .map(CategorisedRequestDocumentView::getDocumentDisplayCategoryId)
        .findFirst()
        .orElseThrow(() -> new DocumentNotAssociatedWithRequestException(documentId, requestId));
  }
}
