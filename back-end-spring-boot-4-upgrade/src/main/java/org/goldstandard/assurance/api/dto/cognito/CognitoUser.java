package org.goldstandard.assurance.api.dto.cognito;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CognitoUser {
  private final UUID sub;
  private final String userName;
  private final String emailAddress;
  private final boolean enabled;
  private final String givenName;
  private final String familyName;
  private final Boolean emailVerified;
  private final Set<String> groups;
}
