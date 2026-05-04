package org.goldstandard.assurance.api.dto.mfa;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateMfaSettingsResponseDTO {
  private final boolean success;
  private final boolean softwareTokenMfaEnabled;
  private final boolean emailMfaEnabled;
}
