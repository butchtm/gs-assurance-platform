package org.goldstandard.assurance.api.dto.auth;

import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResultDTO {
  private final String idToken;
  private final String accessToken;
  private final String refreshToken;
  private final String session;
  private final boolean successful;
  private final boolean newPasswordRequired;
  private final boolean mfaChallengeRequired;
  private final String challengeType; // "SOFTWARE_TOKEN_MFA" or "EMAIL_OTP"
  private final List<String> availableChallengeTypes;

  public Optional<String> getRefreshToken() {
    return Optional.ofNullable(refreshToken);
  }

  public Optional<String> getChallengeType() {
    return Optional.ofNullable(challengeType);
  }
}
