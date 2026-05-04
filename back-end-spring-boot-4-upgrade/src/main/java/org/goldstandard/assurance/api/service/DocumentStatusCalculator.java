package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.goldstandard.assurance.api.entity.RequiredDocumentStatus;
import org.goldstandard.assurance.api.entity.view.CategorisedRequestDocumentView;
import org.goldstandard.assurance.api.repository.CategorisedRequestDocumentViewRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentStatusCalculator {

  private final RequestTypeDocumentTypeService documentTypeService;
  private final CategorisedRequestDocumentViewRepository requestDocumentViewRepository;

  public RequiredDocumentStatus calculateRequiredDocumentsStatus(Request request) {
    var type = request.getRequestType();
    var requiredDocumentTypes = documentTypeService.getRequiredDocumentTypes(type.getId());
    return calculateRequiredDocumentStatus(request, requiredDocumentTypes);
  }

  private RequiredDocumentStatus calculateRequiredDocumentStatus(
      Request request, Collection<RequestTypeDocumentType> requiredTypes) {
    var documents = requestDocumentViewRepository.findAllByRequestId(request.getId());
    return calculateRequiredDocumentStatus(requiredTypes, documents);
  }

  private RequiredDocumentStatus calculateRequiredDocumentStatus(
      Collection<RequestTypeDocumentType> requiredTypes,
      Collection<CategorisedRequestDocumentView> documents) {
    return RequiredDocumentStatus.builder()
        .provided(
            requiredTypes.stream()
                .filter(requiredType -> isProvided(requiredType, documents))
                .count())
        .required(requiredTypes.size())
        .build();
  }

  private boolean isProvided(
      RequestTypeDocumentType requiredType, Collection<CategorisedRequestDocumentView> documents) {
    return documents.stream()
        .anyMatch(
            document ->
                Objects.equals(document.getDocumentTypeId(), requiredType.getDocumentTypeId()));
  }
}
