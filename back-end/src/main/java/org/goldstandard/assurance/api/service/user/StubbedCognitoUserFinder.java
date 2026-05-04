package org.goldstandard.assurance.api.service.user;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.configuration.StubbedCognitoUserConfiguration;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
@RequiredArgsConstructor
public class StubbedCognitoUserFinder implements CognitoUserFinder {

  private final Map<UUID, CognitoUser> users;

  @Autowired
  public StubbedCognitoUserFinder(StubbedCognitoUserConfiguration configuration) {
    this(configuration.getStubbedUsers());
  }

  public Map<UUID, CognitoUser> getCognitoUsers() {
    return users;
  }
}
