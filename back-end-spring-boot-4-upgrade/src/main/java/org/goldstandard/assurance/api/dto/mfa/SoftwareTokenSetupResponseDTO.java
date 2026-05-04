package org.goldstandard.assurance.api.dto.mfa;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class SoftwareTokenSetupResponseDTO {
  private final String secretCode;
  private final String otpAuthUrl;
}
