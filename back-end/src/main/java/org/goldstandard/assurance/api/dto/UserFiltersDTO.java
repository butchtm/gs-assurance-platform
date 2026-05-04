package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserFiltersDTO {

  private final Boolean active;
  private final String searchTerm;
}
