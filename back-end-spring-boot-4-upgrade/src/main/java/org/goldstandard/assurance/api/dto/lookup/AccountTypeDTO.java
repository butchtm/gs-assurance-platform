package org.goldstandard.assurance.api.dto.lookup;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@Builder
public class AccountTypeDTO {
  private final Integer id;
  private final String code;
  private final String description;
  private final Boolean isActive;
}
