package org.goldstandard.assurance.api.service.user;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.UserAlreadyExistsException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local || test")
@Service
@RequiredArgsConstructor
public class StubbedCognitoUserService implements CognitoUserService {

  private final Supplier<UUID> uuidSupplier;
  private final Map<String, User> users;

  @Override
  public User create(UpsertUserRequest request) {
    User user = toUser(request);
    String username = user.getUserName();
    if (users.containsKey(username)) {
      throw new UserAlreadyExistsException(username);
    }
    users.put(username, user);
    return user;
  }

  @Override
  public void update(User user) {
    String username = user.getUserName();
    if (!users.containsKey(username)) {
      throw new NotFoundException("User not found %s in stub cognito", username);
    }
    users.put(username, user);
  }

  @Override
  public boolean exists(String username) {
    return getByUsername(username).isPresent();
  }

  @Override
  public Optional<User> getByUsername(String username) {
    return Optional.ofNullable(users.get(username));
  }

  private User toUser(UpsertUserRequest request) {
    var user = new User();
    user.setIdpUserId(uuidSupplier.get());
    user.setEmailAddress(request.getEmail());
    user.setUserName(request.getUserName());
    user.setGivenName(request.getFirstName());
    user.setFamilyName(request.getLastName());
    user.setGroups(new ArrayList<>(request.getGroups()));
    return user;
  }
}
