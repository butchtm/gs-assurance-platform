package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditingPeriodDTOMother {

  public static CreditingPeriodDTO withId(Integer id) {
    return CreditingPeriodDTO.builder().periodId(id).build();
  }
}
