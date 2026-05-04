package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsultationDTO {

  private Integer id;
  private ConsultationTypeDTO type;
  private ProjectDTO project;
  private RequestDTO request;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
}
