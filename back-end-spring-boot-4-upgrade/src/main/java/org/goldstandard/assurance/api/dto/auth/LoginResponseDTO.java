package org.goldstandard.assurance.api.dto.auth;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.UserDTO;

@RequiredArgsConstructor
@Getter
@Builder
public class LoginResponseDTO {

  private final boolean newPasswordRequired;
  private final boolean mfaChallengeRequired;
  private final String challengeType;
  private final UserDTO user;
  private final List<String> availableChallengeTypes;

  public LoginResponseDTO() {
    this(true, false, null, null, null);
  }

  public LoginResponseDTO(UserDTO user) {
    this(false, false, null, user, null);
  }

  public LoginResponseDTO(boolean mfaChallengeRequired, String challengeType) {
    this(false, mfaChallengeRequired, challengeType, null, null);
  }
}
