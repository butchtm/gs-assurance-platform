package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.RequestRound;

@AllArgsConstructor
@Getter
@Builder
public class RequestRoundDTO {
  private Integer roundIndex;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private Integer statusId;

  public static RequestRoundDTO fromRequestRound(RequestRound requestRound) {
    return RequestRoundDTO.builder()
        .roundIndex(requestRound.getRoundIndex())
        .startDate(requestRound.getStartDate())
        .endDate(requestRound.getEndDate())
        .statusId(requestRound.getStatus().getId())
        .build();
  }
}
