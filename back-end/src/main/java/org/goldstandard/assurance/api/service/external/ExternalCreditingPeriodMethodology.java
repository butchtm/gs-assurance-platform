package org.goldstandard.assurance.api.service.external;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ExternalCreditingPeriodMethodology {

  private final Integer id;
  private final String description;
  private final Double version;
}
