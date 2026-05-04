package org.goldstandard.assurance.api.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MfaChallengeResponseDTO {
  private final String session;
  private final String username;
  private final String code;
  private final String challengeType; // "SOFTWARE_TOKEN_MFA" or "EMAIL_OTP"
  private final String challengeSelection;
}
