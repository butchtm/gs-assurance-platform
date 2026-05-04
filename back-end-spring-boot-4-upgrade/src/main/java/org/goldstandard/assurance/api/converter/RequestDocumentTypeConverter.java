package org.goldstandard.assurance.api.converter;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.goldstandard.assurance.api.dto.RequestTypeDocumentTypeDTO;
import org.goldstandard.assurance.api.entity.RequestTypeDocumentType;
import org.springframework.stereotype.Component;

@Component
public class RequestDocumentTypeConverter {

  public Map<String, Collection<RequestTypeDocumentTypeDTO>> toDtos(
      Map<String, Collection<RequestTypeDocumentType>> types) {
    return types.entrySet().stream()
        .map(RequestDocumentTypeConverter::toEntryDto)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static Map.Entry<String, Collection<RequestTypeDocumentTypeDTO>> toEntryDto(
      Map.Entry<String, Collection<RequestTypeDocumentType>> entry) {
    return new AbstractMap.SimpleEntry<>(entry.getKey(), toTypeDtos(entry.getValue()));
  }

  private static Collection<RequestTypeDocumentTypeDTO> toTypeDtos(
      Collection<RequestTypeDocumentType> types) {
    return types.stream().map(RequestDocumentTypeConverter::toTypeDto).toList();
  }

  private static RequestTypeDocumentTypeDTO toTypeDto(RequestTypeDocumentType type) {
    var documentType = type.getDocumentType();
    var displayCategory = type.getDocumentDisplayCategory();
    return RequestTypeDocumentTypeDTO.builder()
        .requestTypeId(type.getRequestTypeId())
        .documentTypeId(documentType.getId())
        .documentTypeDescription(documentType.getDescription())
        .documentDisplayCategoryId(displayCategory.getId())
        .documentDisplayCategoryCode(displayCategory.getCode())
        .formType(type.getFormType().orElse(null))
        .build();
  }
}
