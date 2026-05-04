package org.goldstandard.assurance.api.dto.lookup;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountTypeDTO {
  private Integer id;
  private String code;
  private String description;
  private boolean isActive;
}
