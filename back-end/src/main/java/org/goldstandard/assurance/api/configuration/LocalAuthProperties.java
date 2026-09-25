package org.goldstandard.assurance.api.configuration;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "local-auth")
public class LocalAuthProperties {
  private String jwtSecret;
  private Map<String, Credential> credentials = new HashMap<>();

  @Getter
  @Setter
  public static class Credential {
    private String password;
    private String databaseUserName;

    public boolean matches(String candidate) {
      return password != null && password.equals(candidate);
    }
  }
}
