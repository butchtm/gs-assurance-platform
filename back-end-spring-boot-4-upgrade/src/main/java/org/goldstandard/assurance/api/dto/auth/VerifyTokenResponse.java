package org.goldstandard.assurance.api.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.UserDTO;

@RequiredArgsConstructor
@Getter
public class VerifyTokenResponse {

  private final boolean authenticated;
  private final UserDTO user;

  public VerifyTokenResponse(UserDTO user) {
    this(true, user);
  }

  public VerifyTokenResponse() {
    this(false, null);
  }
}
