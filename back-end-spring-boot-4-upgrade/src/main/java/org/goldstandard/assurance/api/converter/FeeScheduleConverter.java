package org.goldstandard.assurance.api.converter;

import java.util.Objects;
import org.goldstandard.assurance.api.dto.FeeScheduleDTO;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.springframework.stereotype.Component;

@Component
public class FeeScheduleConverter {

  public FeeScheduleDTO toDTO(FeeSchedule feeSchedule) {
    if (Objects.isNull(feeSchedule)) {
      return null;
    }
    return FeeScheduleDTO.builder()
        .id(feeSchedule.getId())
        .code(feeSchedule.getCode())
        .description(feeSchedule.getDescription())
        .build();
  }
}
