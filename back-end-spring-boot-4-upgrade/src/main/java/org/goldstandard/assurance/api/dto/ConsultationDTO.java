package org.goldstandard.assurance.api.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
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
