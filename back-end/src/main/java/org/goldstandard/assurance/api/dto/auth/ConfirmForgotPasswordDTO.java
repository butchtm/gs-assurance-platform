package org.goldstandard.assurance.api.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Builder
@Getter
public class ConfirmForgotPasswordDTO {
  private final String username;
  private final String newPassword;
  private final String confirmationCode;
}
