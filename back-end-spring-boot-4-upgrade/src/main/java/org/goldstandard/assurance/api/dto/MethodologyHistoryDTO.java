package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MethodologyHistoryDTO {
  private final Integer id;
  private final Integer projectId;
  private final String gsid;
  private final Integer periodId;
  private final Integer requestId;
  private final String requestName;
  private final ZonedDateTime updatedAt;
  private final Integer updatedByAccountId;
  private final String updatedByAccountName;
  private final Collection<MethodologyUpdateDTO> updates;
  private final String verdict;
}
