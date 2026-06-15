package org.goldstandard.assurance.api.service.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

@Service
@Builder
public class DefaultCognitoUserFinder implements CognitoUserFinder {

  private final String userPoolId;
  private final CognitoIdentityProviderClient client;

  @Autowired
  public DefaultCognitoUserFinder(
      @Value("${cognito.user-pool.id}") String userPoolId, CognitoIdentityProviderClient client) {
    this.userPoolId = userPoolId;
    this.client = client;
  }

  public Map<UUID, CognitoUser> getCognitoUsers() {
    Map<UUID, Set<String>> groupsByUser = getGroupsByUser();
    ListUsersRequest request = buildListUsersRequest();
    return client.listUsersPaginator(request).stream()
        .map(ListUsersResponse::users)
        .flatMap(Collection::stream)
        .map(user -> toCognitoUser(user, groupsByUser))
        .collect(Collectors.toMap(CognitoUser::getSub, Function.identity()));
  }

  private CognitoUser toCognitoUser(UserType user, Map<UUID, Set<String>> groupsByUser) {
    CognitoAttributes attributes = new CognitoAttributes(user);
    UUID subject = attributes.getSubject();
    return CognitoUser.builder()
        .sub(subject)
        .userName(user.username())
        .emailAddress(attributes.getEmail())
        .enabled(user.enabled())
        .givenName(attributes.getGivenName())
        .familyName(attributes.getFamilyName())
        .emailVerified(attributes.isEmailVerified())
        .groups(groupsByUser.getOrDefault(subject, Set.of()))
        .build();
  }

  private Map<UUID, Set<String>> getGroupsByUser() {
    Map<UUID, Set<String>> groupsByUser = new HashMap<>();
    for (String groupName : getAllGroupNames()) {
      for (UUID subject : getUserSubjectsInGroup(groupName)) {
        Set<String> userGroups = groupsByUser.getOrDefault(subject, new HashSet<>());
        userGroups.add(groupName);
        groupsByUser.put(subject, userGroups);
      }
    }
    return Collections.unmodifiableMap(groupsByUser);
  }

  private Collection<UUID> getUserSubjectsInGroup(String groupName) {
    return client.listUsersInGroupPaginator(toListUsersInGroupRequest(groupName)).stream()
        .map(ListUsersInGroupResponse::users)
        .flatMap(Collection::stream)
        .map(CognitoAttributes::new)
        .map(CognitoAttributes::getSubject)
        .distinct()
        .toList();
  }

  private Collection<String> getAllGroupNames() {
    return client.listGroupsPaginator(buildListGroupsRequest()).stream()
        .map(ListGroupsResponse::groups)
        .flatMap(Collection::stream)
        .map(GroupType::groupName)
        .distinct()
        .toList();
  }

  private ListUsersRequest buildListUsersRequest() {
    return ListUsersRequest.builder().userPoolId(userPoolId).build();
  }

  private ListGroupsRequest buildListGroupsRequest() {
    return ListGroupsRequest.builder().userPoolId(userPoolId).build();
  }

  private ListUsersInGroupRequest toListUsersInGroupRequest(String groupName) {
    return ListUsersInGroupRequest.builder().userPoolId(userPoolId).groupName(groupName).build();
  }

  @RequiredArgsConstructor
  private static class CognitoAttributes {

    private final Map<String, String> map;

    public CognitoAttributes(UserType user) {
      this(toMap(user.attributes()));
    }

    public UUID getSubject() {
      return UUID.fromString(map.get("sub"));
    }

    public String getEmail() {
      return map.get("email");
    }

    public String getGivenName() {
      return map.get("given_name");
    }

    public String getFamilyName() {
      return map.get("family_name");
    }

    public boolean isEmailVerified() {
      return Boolean.parseBoolean(map.getOrDefault("email_verified", "false"));
    }

    private static Map<String, String> toMap(List<AttributeType> attributes) {
      return attributes.stream()
          .collect(Collectors.toMap(AttributeType::name, AttributeType::value));
    }
  }
}
