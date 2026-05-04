package org.goldstandard.assurance.api.dto.mfa;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerifySoftwareTokenResponseDTO {
  private final boolean verified;
}
