package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestTypeDTO;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestTypeConverter {
  public RequestTypeDTO toDTO(RequestType requestType) {
    return RequestTypeDTO.builder()
        .id(requestType.getId())
        .code(requestType.getCode())
        .description(requestType.getDescription())
        .isActive(requestType.isActive())
        .supportsPAA(requestType.supportsPAA())
        .build();
  }
}
