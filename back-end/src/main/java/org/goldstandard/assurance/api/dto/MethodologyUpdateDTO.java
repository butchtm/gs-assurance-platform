package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MethodologyUpdateDTO {
  private final Integer id;
  private final Double originalVersion;
  private final Double updatedVersion;
  private final String operation;
}
