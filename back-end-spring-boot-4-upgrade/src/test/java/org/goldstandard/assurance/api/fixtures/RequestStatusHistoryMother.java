package org.goldstandard.assurance.api.fixtures;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.RequestStatusHistory;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestStatusHistoryMother {

  private static final Integer DEFAULT_ID = 100;

  public static RequestStatusHistory inProgress(ZonedDateTime changedAt) {
    return build(RequestStatus.inProgress(), changedAt);
  }

  public static RequestStatusHistory build(RequestStatus status, ZonedDateTime changedAt) {
    return RequestStatusHistory.builder()
        .id(DEFAULT_ID)
        .nextStatus(status)
        .changedAt(changedAt)
        .build();
  }
}
