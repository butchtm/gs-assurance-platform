package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Getter
public class CreditingPeriodMethodologyDTO {
  private final Integer id;
  private final String code;
  private final String description;
  private final Double version;
}
