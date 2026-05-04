package org.goldstandard.assurance.api.service.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.exception.NoAssignedGroupsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ClaimsExtractor {

  public Collection<String> extractGroups(Jwt jwt) {
    var claims = jwt.getClaims();
    var groups = extractCognitoGroups(claims);
    if (CollectionUtils.isEmpty(groups)) {
      var username = extractUsername(claims);
      throw new NoAssignedGroupsException(username);
    }
    return groups;
  }

  private static Collection<String> extractCognitoGroups(Map<String, Object> claims) {
    var groups = claims.getOrDefault("cognito:groups", Collections.emptyList());
    if (groups instanceof Collection<?> collection) {
      return collection.stream().map(Object::toString).toList();
    }
    return Collections.emptyList();
  }

  private static String extractUsername(Map<String, Object> claims) {
    return claims.getOrDefault("username", "not-provided").toString();
  }
}
