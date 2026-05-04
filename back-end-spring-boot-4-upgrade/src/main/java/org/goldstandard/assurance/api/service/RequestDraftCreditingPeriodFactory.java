package org.goldstandard.assurance.api.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestDraftCreditingPeriodFactory {

  public Optional<RequestCreditingPeriod> build(CreditingPeriodDTO dto) {
    return build(null, dto);
  }

  public Optional<RequestCreditingPeriod> build(Integer requestId, CreditingPeriodDTO dto) {
    if (Objects.isNull(dto)
        || Objects.isNull(dto.getStartDate())
        || Objects.isNull(dto.getEndDate())) {
      return Optional.empty();
    }
    return Optional.of(
        RequestCreditingPeriod.builder()
            .requestId(requestId)
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .build());
  }
}
