package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsultationFiltersDTO {

  private final ZonedDateTime liveAt;
  private final Integer consultationTypeId;
}
