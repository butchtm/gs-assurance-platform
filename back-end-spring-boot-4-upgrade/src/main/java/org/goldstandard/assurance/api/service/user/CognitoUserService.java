package org.goldstandard.assurance.api.service.user;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;

public interface CognitoUserService {

  void update(User user);

  User create(UpsertUserRequest request);

  boolean exists(String username);

  Optional<User> getByUsername(String username);
}
