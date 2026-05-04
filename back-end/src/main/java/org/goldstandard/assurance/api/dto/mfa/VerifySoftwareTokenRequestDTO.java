package org.goldstandard.assurance.api.dto.mfa;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerifySoftwareTokenRequestDTO {
  private final String code;
  private final Boolean emailMfaEnabled;
}
