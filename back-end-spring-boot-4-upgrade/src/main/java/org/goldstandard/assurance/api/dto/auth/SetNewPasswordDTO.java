package org.goldstandard.assurance.api.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Getter
public class SetNewPasswordDTO {
  private final String username;
  private final String newPassword;
  private final String givenName;
  private final String familyName;
  @With private final String sessionId;
}
