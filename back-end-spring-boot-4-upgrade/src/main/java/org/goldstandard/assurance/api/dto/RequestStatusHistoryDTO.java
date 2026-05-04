package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;

@Getter
@Builder
public class RequestStatusHistoryDTO {

  private Integer id;
  private Integer previousStatusId;
  private Integer nextStatusId;
  private ZonedDateTime changedAt;

  public static RequestStatusHistoryDTO fromRequestStatusHistory(
      RequestStatusHistory requestStatusHistory) {
    return RequestStatusHistoryDTO.builder()
        .id(requestStatusHistory.getId())
        .previousStatusId(
            requestStatusHistory.getPreviousStatus() != null
                ? requestStatusHistory.getPreviousStatus().getId()
                : null)
        .nextStatusId(
            requestStatusHistory.getNextStatus() != null
                ? requestStatusHistory.getNextStatus().getId()
                : null)
        .changedAt(requestStatusHistory.getChangedAt())
        .build();
  }
}
