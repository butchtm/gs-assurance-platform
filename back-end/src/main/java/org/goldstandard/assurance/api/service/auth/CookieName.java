package org.goldstandard.assurance.api.service.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieName {
  public static final String ID_TOKEN = "idToken";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final String REFRESH_TOKEN = "refreshToken";
  public static final String PASSWORD_SESSION_ID = "passwordSessionId";
  public static final String MFA_SESSION_ID = "mfaSessionId";
}
