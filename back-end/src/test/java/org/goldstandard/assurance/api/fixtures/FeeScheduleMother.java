package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeeScheduleMother {

  public static FeeSchedule withCode(String code) {
    return builder().code(code).build();
  }

  public static FeeSchedule.FeeScheduleBuilder<?, ?> builder() {
    return FeeSchedule.builder().id(0).code("CASH_PER_CREDIT").description("Cash per Credit");
  }
}
