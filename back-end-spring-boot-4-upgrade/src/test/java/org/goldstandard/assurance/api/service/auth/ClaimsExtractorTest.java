package org.goldstandard.assurance.api.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.exception.NoAssignedGroupsException;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class ClaimsExtractorTest {

  private final ClaimsExtractor extractor = new ClaimsExtractor();

  @Test
  void shouldThrowExceptionWithNotProvidedUsernameIfCognitoGroupsAndUsernameNotPresentInClaims() {
    var jwt = givenJwtWithClaims(Map.of());

    var error = catchThrowable(() -> extractor.extractGroups(jwt));

    assertThat(error)
        .isInstanceOf(NoAssignedGroupsException.class)
        .hasMessage("User not-provided has no roles assigned");
  }

  @Test
  void shouldThrowExceptionIfCognitoGroupsNotCollectionInJwtClaims() {
    var jwt = givenJwtWithClaims(Map.of("username", "test-user"));

    var error = catchThrowable(() -> extractor.extractGroups(jwt));

    assertThat(error)
        .isInstanceOf(NoAssignedGroupsException.class)
        .hasMessage("User test-user has no roles assigned");
  }

  @Test
  void shouldReturnECognitoGroupsPresentInJwtClaims() {
    var expectedGroups = List.of("group-1", "group-2");
    var jwt = givenJwtWithClaims(Map.of("cognito:groups", expectedGroups));

    var groups = extractor.extractGroups(jwt);

    assertThat(groups).isEqualTo(expectedGroups);
  }

  private Jwt givenJwtWithClaims(Map<String, Object> claims) {
    var jwt = mock(Jwt.class);
    when(jwt.getClaims()).thenReturn(claims);
    return jwt;
  }
}
