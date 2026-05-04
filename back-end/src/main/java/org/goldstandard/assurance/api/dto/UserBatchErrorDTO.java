package org.goldstandard.assurance.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Data
public class UserBatchErrorDTO {

  private final String userName;
  private final String message;
  private final String account;
}
