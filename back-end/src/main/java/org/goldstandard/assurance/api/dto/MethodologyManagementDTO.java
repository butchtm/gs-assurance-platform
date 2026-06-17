package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class MethodologyManagementDTO {
  private final Integer id;
  private final String code;
  private final String description;
  private final Boolean active;
  private final boolean inUse;
  private final boolean paa;
}
