package org.goldstandard.assurance.api.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UserBatchError {

  private final String userName;
  private final String message;
  private final String account;
}
