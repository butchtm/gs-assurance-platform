package org.goldstandard.assurance.api.service.user;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.InvalidUserGroupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.AdminListGroupsForUserIterable;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.ListGroupsIterable;

@Service
@Slf4j
public class CognitoGroupService {

  private final String userPoolId;
  private final CognitoIdentityProviderClient client;

  @Autowired
  public CognitoGroupService(
      @Value("${cognito.user-pool.id}") String userPoolId, CognitoIdentityProviderClient client) {
    this.userPoolId = userPoolId;
    this.client = client;
  }

  public void validateGroups(Collection<String> inputGroups) {
    Collection<String> allGroups = getAllGroups();
    var invalidGroups =
        inputGroups.stream().filter(inputGroup -> !allGroups.contains(inputGroup)).toList();
    if (invalidGroups.isEmpty()) {
      return;
    }
    throw new InvalidUserGroupException(String.join(",", invalidGroups));
  }

  public void updateUserGroups(User user) {
    Collection<String> existingGroups = getGroupsForUser(user.getUserName());

    Collection<String> groupsToAdd = CollectionUtils.subtract(user.getGroups(), existingGroups);
    addUserToGroups(user.getUserName(), groupsToAdd);

    Collection<String> groupsToRemove = CollectionUtils.subtract(existingGroups, user.getGroups());
    removeUserFromGroups(user.getUserName(), groupsToRemove);
  }

  public Collection<String> getGroupsForUser(String username) {
    AdminListGroupsForUserRequest request =
        AdminListGroupsForUserRequest.builder().userPoolId(userPoolId).username(username).build();
    AdminListGroupsForUserIterable responses = client.adminListGroupsForUserPaginator(request);
    return responses.stream()
        .map(AdminListGroupsForUserResponse::groups)
        .flatMap(Collection::stream)
        .map(GroupType::groupName)
        .toList();
  }

  public void addUserToGroups(String username, Collection<String> groups) {
    groups.forEach(group -> addUserToGroup(username, group));
  }

  private void addUserToGroup(String username, String group) {
    AdminAddUserToGroupRequest request =
        AdminAddUserToGroupRequest.builder()
            .userPoolId(userPoolId)
            .username(username)
            .groupName(group)
            .build();
    client.adminAddUserToGroup(request);
    log.info("added user {} to group {}", username, group);
  }

  private void removeUserFromGroups(String username, Collection<String> groups) {
    groups.forEach(group -> removeUserFromGroup(username, group));
  }

  private void removeUserFromGroup(String username, String group) {
    AdminRemoveUserFromGroupRequest request =
        AdminRemoveUserFromGroupRequest.builder()
            .userPoolId(userPoolId)
            .username(username)
            .groupName(group)
            .build();
    client.adminRemoveUserFromGroup(request);
    log.info("added user {} to group {}", username, group);
  }

  private Collection<String> getAllGroups() {
    ListGroupsRequest request = ListGroupsRequest.builder().userPoolId(userPoolId).build();
    ListGroupsIterable responses = client.listGroupsPaginator(request);
    return responses.stream()
        .map(ListGroupsResponse::groups)
        .flatMap(Collection::stream)
        .map(GroupType::groupName)
        .toList();
  }
}
