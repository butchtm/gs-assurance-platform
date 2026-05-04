package org.goldstandard.assurance.api.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Getter
public class LoginRequestDTO {
  private final String username;
  private final String password;
}
