package org.goldstandard.assurance.api.service.user;

import java.util.Map;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;

public interface CognitoUserFinder {
  Map<UUID, CognitoUser> getCognitoUsers();
}
