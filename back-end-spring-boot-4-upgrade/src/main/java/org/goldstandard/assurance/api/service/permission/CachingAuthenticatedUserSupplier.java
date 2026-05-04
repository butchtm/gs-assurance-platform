package org.goldstandard.assurance.api.service.permission;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;

@RequiredArgsConstructor
public class CachingAuthenticatedUserSupplier implements AuthenticatedUserSupplier {

  private final AuthenticatedUserSupplier supplier;
  private User authenticatedUser;
  private String accessToken;

  @Override
  public Optional<User> getAuthenticatedUser() {
    if (Objects.isNull(authenticatedUser)) {
      authenticatedUser = supplier.getAuthenticatedUser().orElse(null);
    }
    return Optional.ofNullable(authenticatedUser);
  }

  @Override
  public Set<String> getAuthenticatedUserRoles() {
    return supplier.getAuthenticatedUserRoles();
  }

  @Override
  public Optional<String> getAuthenticatedAccessToken() {
    if (Objects.isNull(accessToken)) {
      accessToken = supplier.getAuthenticatedAccessToken().orElse(null);
    }
    return Optional.ofNullable(accessToken);
  }
}
