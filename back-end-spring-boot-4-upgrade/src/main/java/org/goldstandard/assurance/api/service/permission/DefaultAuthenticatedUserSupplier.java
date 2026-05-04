package org.goldstandard.assurance.api.service.permission;

import static org.goldstandard.assurance.api.configuration.security.SecurityConfiguration.ROLE_PREFIX;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RequiredArgsConstructor
public class DefaultAuthenticatedUserSupplier implements AuthenticatedUserSupplier {

  private final UserRepository repository;

  @Override
  public Optional<User> getAuthenticatedUser() {
    return getAuthenticatedUserId().flatMap(repository::findByIdpUserId);
  }

  @Override
  public Set<String> getAuthenticatedUserRoles() {
    return getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .map(DefaultAuthenticatedUserSupplier::removeRolePrefix)
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<String> getAuthenticatedAccessToken() {
    return getAuthenticatedJwt().map(AbstractOAuth2Token::getTokenValue);
  }

  private static Optional<UUID> getAuthenticatedUserId() {
    return getAuthenticatedJwt().map(jwt -> UUID.fromString(jwt.getSubject()));
  }

  private static Optional<Jwt> getAuthenticatedJwt() {
    var auth = getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwt) {
      return Optional.of(jwt.getToken());
    }
    return Optional.empty();
  }

  private static String removeRolePrefix(String authority) {
    if (authority.startsWith(ROLE_PREFIX)) {
      return authority.substring(ROLE_PREFIX.length());
    }
    return authority;
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
