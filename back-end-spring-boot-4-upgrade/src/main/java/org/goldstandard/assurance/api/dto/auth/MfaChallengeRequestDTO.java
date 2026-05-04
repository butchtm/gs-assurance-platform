package org.goldstandard.assurance.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaChallengeRequestDTO {
  private String username;
  private String code;
  private String challengeType;
}
