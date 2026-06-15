package org.goldstandard.assurance.api.dto;

import lombok.*;

@Builder
@Getter
@Setter
public class RequestTypeDTO {
  private Integer id;
  private String code;
  private String description;
  private Boolean isActive;
  private boolean supportsPAA;
}
