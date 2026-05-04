package org.goldstandard.assurance.api.converter;

import org.goldstandard.assurance.api.dto.RequestStatusDTO;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.springframework.stereotype.Component;

@Component
public class RequestStatusConverter {

  public RequestStatusDTO toDTO(RequestStatus requestStatus) {
    return RequestStatusDTO.builder()
        .id(requestStatus.getId())
        .code(requestStatus.getCode())
        .description(requestStatus.getDescription())
        .build();
  }
}
