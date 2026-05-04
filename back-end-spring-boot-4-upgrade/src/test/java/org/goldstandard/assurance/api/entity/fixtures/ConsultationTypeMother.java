package org.goldstandard.assurance.api.entity.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.ConsultationType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsultationTypeMother {

  public static ConsultationType tacType() {
    ConsultationType type = new ConsultationType(ConsultationType.TAC_CONSULTATION);
    type.setUserType("TAC");
    type.setWorkingDays(7);
    return type;
  }

  public static ConsultationType publicType() {
    ConsultationType type = new ConsultationType(ConsultationType.PUBLIC_CONSULTATION);
    type.setUserType("PUBLIC");
    type.setWorkingDays(1);
    return type;
  }
}
