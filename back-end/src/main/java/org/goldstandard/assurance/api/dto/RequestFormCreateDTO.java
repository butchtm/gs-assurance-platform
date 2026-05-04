package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RequestFormCreateDTO {
  private final int typeId;
}
