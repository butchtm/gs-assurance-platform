package org.goldstandard.assurance.api.service.user;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

@Service
@Profile("!local && !test")
@Slf4j
public class DefaultCognitoUserService implements CognitoUserService {

  private final CognitoIdentityProviderClient client;
  private final CognitoGroupService groupService;
  private final CognitoUserConverter converter;
  private final String userPoolId;

  public DefaultCognitoUserService(
      CognitoIdentityProviderClient client,
      CognitoGroupService groupService,
      CognitoUserConverter converter,
      @Value("${cognito.user-pool.id}") String userPoolId) {
    this.client = client;
    this.groupService = groupService;
    this.converter = converter;
    this.userPoolId = userPoolId;
  }

  @Override
  public void update(User user) {
    log.info("attempting to update user {}", user.getUserName());
    groupService.validateGroups(user.getGroups());
    AdminUpdateUserAttributesRequest cognitoRequest =
        AdminUpdateUserAttributesRequest.builder()
            .userPoolId(userPoolId)
            .username(user.getUserName())
            .userAttributes(converter.toAttributes(user))
            .build();
    client.adminUpdateUserAttributes(cognitoRequest);
    groupService.updateUserGroups(user);
  }

  @Override
  public User create(UpsertUserRequest request) {
    try {
      log.info("attempting to create user {}", request.getUserName());
      groupService.validateGroups(request.getGroups());
      AdminCreateUserRequest cognitoRequest =
          AdminCreateUserRequest.builder()
              .userPoolId(userPoolId)
              .username(request.getUserName())
              .userAttributes(converter.toAttributes(request))
              .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
              .forceAliasCreation(true)
              .build();
      AdminCreateUserResponse response = client.adminCreateUser(cognitoRequest);
      User user = converter.toUser(response.user(), request.getGroups());
      log.info("user {} created with id {}", user.getUserName(), user.getId());
      groupService.addUserToGroups(user.getUserName(), request.getGroups());
      return user;
    } catch (UsernameExistsException e) {
      throw new UserAlreadyExistsException(request.getUserName(), e);
    }
  }

  @Override
  public boolean exists(String username) {
    return getUsersByUsername(username).findFirst().isPresent();
  }

  @Override
  public Optional<User> getByUsername(String username) {
    return getUsersByUsername(username)
        .findFirst()
        .map(user -> converter.toUser(user, groupService.getGroupsForUser(username)));
  }

  private Stream<UserType> getUsersByUsername(String username) {
    ListUsersRequest usersRequest =
        ListUsersRequest.builder()
            .userPoolId(userPoolId)
            .filter(CognitoUserConverter.toUsernameFilter(username))
            .build();
    ListUsersResponse response = client.listUsers(usersRequest);
    return response.users().stream();
  }
}
