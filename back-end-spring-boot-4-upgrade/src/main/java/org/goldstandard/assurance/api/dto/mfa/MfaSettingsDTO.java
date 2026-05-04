package org.goldstandard.assurance.api.dto.mfa;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class MfaSettingsDTO {
  private final String username;
  private final boolean softwareTokenMfaEnabled;
  private final boolean emailMfaEnabled;
}
