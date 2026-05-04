package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.goldstandard.assurance.api.service.user.CognitoUserFinder;
import org.goldstandard.assurance.api.service.user.DefaultCognitoUserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.ListGroupsIterable;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.ListUsersInGroupIterable;
import software.amazon.awssdk.services.cognitoidentityprovider.paginators.ListUsersIterable;

class DefaultCognitoUserFinderTest {

  private static final String USER_POOL_ID = "test-user-pool-id";

  private static final UUID SUBJECT_1 = UUID.fromString("b1199829-29fd-471b-a6ce-460eb110e2e6");
  private static final UserType USER_1 = UserTypeMother.user1(SUBJECT_1.toString());

  private static final UUID SUBJECT_2 = UUID.fromString("e386feef-5749-4226-94a8-73c082e57423");
  private static final UserType USER_2 = UserTypeMother.user2(SUBJECT_2.toString());

  private static final UUID SUBJECT_3 = UUID.fromString("0f8ba85d-6bbb-447f-995f-693828680ef6");
  private static final UserType USER_3 = UserTypeMother.user3(SUBJECT_3.toString());

  private static final String GROUP_1 = "group-1";
  private static final String GROUP_2 = "group-2";
  private static final String GROUP_3 = "group-3";

  private final CognitoIdentityProviderClient client = mock(CognitoIdentityProviderClient.class);

  private final CognitoUserFinder finder =
      DefaultCognitoUserFinder.builder().userPoolId(USER_POOL_ID).client(client).build();

  @BeforeEach
  public void setUp() {
    mockGroups();
    mockUsers();
    mockUsersInGroups();
  }

  @Test
  void shouldReturnAllUsers() {
    Map<UUID, CognitoUser> users = finder.getCognitoUsers();

    assertThat(users).containsKeys(SUBJECT_1, SUBJECT_2, SUBJECT_3);
  }

  @Test
  void shouldPopulateUserFieldsCorrectly() {
    Map<UUID, CognitoUser> users = finder.getCognitoUsers();

    CognitoUser user = users.get(SUBJECT_1);

    assertThat(user.getSub()).isEqualTo(SUBJECT_1);
    assertThat(user.getUserName()).isEqualTo("user-1");
    assertThat(user.getGivenName()).isEqualTo("user-1-given-name");
    assertThat(user.getFamilyName()).isEqualTo("user-1-family-name");
    assertThat(user.getEmailAddress()).isEqualTo("user-1@email.com");
    assertThat(user.getEmailVerified()).isTrue();
    assertThat(user.getGroups()).containsExactly(GROUP_1);
    assertThat(user.isEnabled()).isTrue();
  }

  @Test
  void shouldPopulateUserWithMultipleGroups() {
    Map<UUID, CognitoUser> users = finder.getCognitoUsers();

    CognitoUser user = users.get(SUBJECT_2);

    assertThat(user.getSub()).isEqualTo(SUBJECT_2);
    assertThat(user.getUserName()).isEqualTo("user-2");
    assertThat(user.getGivenName()).isNull();
    assertThat(user.getFamilyName()).isNull();
    assertThat(user.getEmailAddress()).isNull();
    assertThat(user.getEmailVerified()).isFalse();
    assertThat(user.getGroups()).containsExactlyInAnyOrder(GROUP_1, GROUP_2);
    assertThat(user.isEnabled()).isFalse();
  }

  @Test
  void shouldPopulateUserWithNoGroups() {
    Map<UUID, CognitoUser> users = finder.getCognitoUsers();

    CognitoUser user = users.get(SUBJECT_3);

    assertThat(user.getSub()).isEqualTo(SUBJECT_3);
    assertThat(user.getUserName()).isEqualTo("user-3");
    assertThat(user.getGivenName()).isNull();
    assertThat(user.getFamilyName()).isNull();
    assertThat(user.getEmailAddress()).isNull();
    assertThat(user.getEmailVerified()).isFalse();
    assertThat(user.getGroups()).isEmpty();
    assertThat(user.isEnabled()).isFalse();
  }

  private void mockGroups() {
    ListGroupsResponse response1 = givenListGroupsResponse(GROUP_1);
    ListGroupsResponse response2 = givenListGroupsResponse(GROUP_2, GROUP_3);
    ListGroupsIterable responses = givenListGroupsResponses(response1, response2);
    ListGroupsRequest request = ListGroupsRequest.builder().userPoolId(USER_POOL_ID).build();
    when(client.listGroupsPaginator(request)).thenReturn(responses);
  }

  private List<GroupType> toGroups(String... names) {
    return Arrays.stream(names).map(this::toGroup).toList();
  }

  private GroupType toGroup(String name) {
    GroupType group = mock(GroupType.class);
    when(group.groupName()).thenReturn(name);
    return group;
  }

  private ListGroupsResponse givenListGroupsResponse(String... groupNames) {
    List<GroupType> groups = toGroups(groupNames);
    ListGroupsResponse response = mock(ListGroupsResponse.class);
    when(response.groups()).thenReturn(groups);
    return response;
  }

  private ListGroupsIterable givenListGroupsResponses(ListGroupsResponse... responses) {
    ListGroupsIterable iterable = mock(ListGroupsIterable.class);
    when(iterable.stream()).thenReturn(Arrays.stream(responses));
    return iterable;
  }

  private void mockUsers() {
    ListUsersResponse response1 = givenListUsersResponse(USER_1);
    ListUsersResponse response2 = givenListUsersResponse(USER_2, USER_3);
    ListUsersIterable responses = givenListUsersResponses(response1, response2);
    ListUsersRequest request = ListUsersRequest.builder().userPoolId(USER_POOL_ID).build();
    when(client.listUsersPaginator(request)).thenReturn(responses);
  }

  private ListUsersResponse givenListUsersResponse(UserType... users) {
    ListUsersResponse response = mock(ListUsersResponse.class);
    when(response.users()).thenReturn(List.of(users));
    return response;
  }

  private ListUsersIterable givenListUsersResponses(ListUsersResponse... responses) {
    ListUsersIterable iterable = mock(ListUsersIterable.class);
    when(iterable.stream()).thenReturn(Arrays.stream(responses));
    return iterable;
  }

  private void mockUsersInGroups() {
    mockUsersInGroup(GROUP_1, USER_1, USER_2);
    mockUsersInGroup(GROUP_2, USER_2);
    mockUsersInGroup(GROUP_3);
  }

  private void mockUsersInGroup(String groupName, UserType... users) {
    ListUsersInGroupResponse response1 = givenListUsersInGroupResponse(users);
    ListUsersInGroupIterable responses1 = givenListUsersInGroupResponses(response1);
    ListUsersInGroupRequest request1 =
        ListUsersInGroupRequest.builder().userPoolId(USER_POOL_ID).groupName(groupName).build();
    when(client.listUsersInGroupPaginator(request1)).thenReturn(responses1);
  }

  private ListUsersInGroupResponse givenListUsersInGroupResponse(UserType... users) {
    ListUsersInGroupResponse response = mock(ListUsersInGroupResponse.class);
    when(response.users()).thenReturn(List.of(users));
    return response;
  }

  private ListUsersInGroupIterable givenListUsersInGroupResponses(
      ListUsersInGroupResponse... responses) {
    ListUsersInGroupIterable iterable = mock(ListUsersInGroupIterable.class);
    when(iterable.stream()).thenReturn(Arrays.stream(responses));
    return iterable;
  }
}
