package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsultationTypeDTO {

  private Integer id;
  private String userType;
  private Integer workingDays;
}
