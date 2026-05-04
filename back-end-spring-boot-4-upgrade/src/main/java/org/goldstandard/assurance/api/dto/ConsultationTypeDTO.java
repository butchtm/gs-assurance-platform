package org.goldstandard.assurance.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConsultationTypeDTO {

  private Integer id;
  private String userType;
  private Integer workingDays;
}
