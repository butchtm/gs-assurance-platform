package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.OTHER_DISPLAY_CATEGORY_ID;
import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.REQUIRED_DISPLAY_CATEGORY_ID;
import static org.goldstandard.assurance.api.entity.lookup.DocumentDisplayCategory.SUPPORTING_DISPLAY_CATEGORY_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.goldstandard.assurance.api.entity.lookup.DocumentType;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.repository.RequestTypeDocumentTypeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestTypeDocumentTypeService {

  private final RequestTypeDocumentTypeRepository requestDocumentTypeRepository;

  public Map<String, Collection<RequestTypeDocumentType>> getDocumentTypesForRequestType(
      Integer requestTypeId) {
    return getRequiredDocumentTypes(requestTypeId).stream()
        .collect(
            Collectors.groupingBy(
                RequestTypeDocumentType::getDocumentDisplayCategoryCode,
                Collectors.mapping(
                    categoryCode -> categoryCode, Collectors.toCollection(ArrayList::new))));
  }

  public Collection<RequestTypeDocumentType> getRequiredDocumentTypes(Integer requestTypeId) {
    return requestDocumentTypeRepository.findAllByRequestTypeIdAndDocumentDisplayCategoryId(
        requestTypeId, REQUIRED_DISPLAY_CATEGORY_ID);
  }

  public Integer findDocumentDisplayCategoryId(Integer requestTypeId, Integer documentTypeId) {
    return requestDocumentTypeRepository
        .findByRequestTypeIdAndDocumentTypeId(requestTypeId, documentTypeId)
        .map(RequestTypeDocumentType::getDocumentDisplayCategory)
        .map(ReferenceDataType::getId)
        .orElseGet(() -> toDefaultDisplayCategoryId(new DocumentType(documentTypeId)));
  }

  private static Integer toDefaultDisplayCategoryId(DocumentType documentType) {
    if (documentType.isOther()) {
      return OTHER_DISPLAY_CATEGORY_ID;
    }
    return SUPPORTING_DISPLAY_CATEGORY_ID;
  }
}
