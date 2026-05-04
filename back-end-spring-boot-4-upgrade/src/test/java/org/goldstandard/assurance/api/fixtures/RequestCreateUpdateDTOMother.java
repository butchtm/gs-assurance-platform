package org.goldstandard.assurance.api.fixtures;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO.RequestCreateUpdateDTOBuilder;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestCreateUpdateDTOMother {

  public static RequestCreateUpdateDTO build() {
    return build(1);
  }

  public static RequestCreateUpdateDTO build(Integer requestId) {
    return builder(requestId).build();
  }

  public static RequestCreateUpdateDTO build(
      Integer requestId, Integer cancellationId, String otherCancellationReason) {
    return builder(requestId)
        .cancellationReasonId(cancellationId)
        .otherCancellationReason(otherCancellationReason)
        .build();
  }

  public static RequestCreateUpdateDTO.RequestCreateUpdateDTOBuilder updateBuilder() {
    return RequestCreateUpdateDTO.builder()
        .creditingPeriod(CreditingPeriodDTO.builder().periodId(1).build())
        .emissionReductionAmount(789);
  }

  public static RequestCreateUpdateDTOBuilder builder(Integer requestId) {
    ZonedDateTime start = ZonedDateTime.now();
    return RequestCreateUpdateDTO.builder()
        .id(requestId)
        .requestStatusId(3)
        .startDate(start)
        .creditingPeriod(CreditingPeriodDTOMother.withId(123))
        .monitoringPeriod(MonitoringPeriodDTOMother.withId(456))
        .emissionReductionAmount(789)
        .manuallyAdded(true)
        .fastTrack(false)
        .internal(true)
        .methodologies(List.of(methodology(1), methodology(2)));
  }

  private static RequestMethodologyDTO methodology(Integer index) {
    return RequestMethodologyDTO.builder()
        .methodologyId(1000 + index)
        .methodologyVersion(index.doubleValue())
        .build();
  }
}
