package org.goldstandard.assurance.api.fixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MonitoringPeriodDTOMother {

  public static MonitoringPeriodDTO withId(Integer id) {
    return MonitoringPeriodDTO.builder().id(id).build();
  }
}
