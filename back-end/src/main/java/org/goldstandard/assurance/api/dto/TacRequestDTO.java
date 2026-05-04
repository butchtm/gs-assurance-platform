package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class TacRequestDTO {
  private final Integer id;
  private final String projectGsid;
  private final String projectName;
  private final String requestName;
  private final RequestStatus requestStatus;
  private final Integer vvbAccountId;
  private final String vvbAccountName;
  private final ZonedDateTime lastRequestStatusChangeDate;
}
