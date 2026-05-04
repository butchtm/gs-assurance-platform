package org.goldstandard.assurance.api.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.permission.DefaultAuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticatedUserSupplierTest {

  @InjectMocks DefaultAuthenticatedUserSupplier supplier;

  @Mock UserRepository userRepository;

  @Test
  void shouldReturnAuthenticatedUser() {
    var subject = UUID.fromString("85daba3d-e567-4e45-adba-838ad84981ec");
    givenAuthentication(jwtWithSubject(subject));
    var expectedUser = givenUserWithIdpId(subject);
    when(userRepository.findByIdpUserId(subject)).thenReturn(Optional.of(expectedUser));

    var user = supplier.getAuthenticatedUser();

    assertThat(user).contains(expectedUser);
  }

  @Test
  void shouldNotReturnAuthenticatedUserIfNoAuthenticationPresent() {
    givenAuthentication(null);

    var user = supplier.getAuthenticatedUser();

    assertThat(user).isEmpty();
  }

  @Test
  void shouldNotReturnAuthenticatedUserIfUserNotFound() {
    var subject = UUID.fromString("85daba3d-e567-4e45-adba-838ad84981ec");
    givenAuthentication(jwtWithSubject(subject));
    when(userRepository.findByIdpUserId(subject)).thenReturn(Optional.empty());

    var user = supplier.getAuthenticatedUser();

    assertThat(user).isEmpty();
  }

  @Test
  void shouldReturnTrueIfMatchesAuthenticatedUserIdpId() {
    var subject = UUID.fromString("85daba3d-e567-4e45-adba-838ad84981ec");
    givenAuthentication(jwtWithSubject(subject));
    var expectedUser = givenUserWithIdpId(subject);
    when(userRepository.findByIdpUserId(subject)).thenReturn(Optional.of(expectedUser));

    var result = supplier.isAuthenticatedUser(subject);

    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseIfNotAuthenticationPresent() {
    var subject = UUID.fromString("85daba3d-e567-4e45-adba-838ad84981ec");
    givenAuthentication(null);

    var result = supplier.isAuthenticatedUser(subject);

    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseIfUserNotFound() {
    var subject = UUID.fromString("85daba3d-e567-4e45-adba-838ad84981ec");
    givenAuthentication(jwtWithSubject(subject));
    when(userRepository.findByIdpUserId(subject)).thenReturn(Optional.empty());

    var result = supplier.isAuthenticatedUser(subject);

    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnAuthenticatedUserRoles() {
    givenAuthentication(jwtWithRoles("ROLE_test-role-1", "test-role-2"));

    var roles = supplier.getAuthenticatedUserRoles();

    assertThat(roles).containsExactly("test-role-1", "test-role-2");
  }

  private static void givenAuthentication(Authentication authentication) {
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  private static Authentication jwtWithSubject(UUID subject) {
    var jwt = mock(Jwt.class);
    when(jwt.getSubject()).thenReturn(subject.toString());
    var authentication = mock(JwtAuthenticationToken.class);
    when(authentication.getToken()).thenReturn(jwt);
    return authentication;
  }

  private static User givenUserWithIdpId(UUID idpId) {
    var user = new User();
    user.setIdpUserId(idpId);
    return user;
  }

  private static JwtAuthenticationToken jwtWithRoles(String... roles) {
    JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
    Collection<GrantedAuthority> authorities =
        Stream.of(roles)
            .map(SimpleGrantedAuthority::new)
            .map(auth -> (GrantedAuthority) auth)
            .toList();
    when(authentication.getAuthorities()).thenReturn(authorities);
    return authentication;
  }
}
