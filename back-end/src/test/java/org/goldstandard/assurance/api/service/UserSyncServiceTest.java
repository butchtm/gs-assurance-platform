package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.goldstandard.assurance.api.service.user.CognitoUserFinder;
import org.goldstandard.assurance.api.service.user.UserSyncRunPolicy;
import org.goldstandard.assurance.api.service.user.UserSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSyncServiceTest {

  private static final UUID SUBJECT = UUID.fromString("c2f62d77-b205-4882-8204-89052d1271fe");

  @InjectMocks UserSyncService syncService;

  @Mock ProcessService processService = mock(ProcessService.class);

  @Mock UserRepository userRepository = mock(UserRepository.class);

  @Mock UserSyncRunPolicy runPolicy = mock(UserSyncRunPolicy.class);

  @Mock CognitoUserFinder userService = mock(CognitoUserFinder.class);

  @Captor ArgumentCaptor<Collection<User>> userCaptor;

  @Test
  void shouldNotRunIfNotRequired() {
    when(runPolicy.shouldRun()).thenReturn(false);

    syncService.syncUsersIfRequired();

    verifyNoInteractions(userService, userRepository);
  }

  @Test
  void shouldPersistProcessStartAndEndTimes() {
    when(runPolicy.shouldRun()).thenReturn(true);
    Process process = mock(Process.class);
    when(processService.recordUserSyncProcessStarted()).thenReturn(process);

    syncService.syncUsersIfRequired();

    verify(processService).recordProcessComplete(process);
  }

  @Test
  void shouldPersistProcessStartAndEndTimesWhenSyncProcessThrowsException() {
    when(runPolicy.shouldRun()).thenReturn(true);
    var expectedError = new RuntimeException("boom");
    when(userService.getCognitoUsers()).thenThrow(expectedError);
    Process process = mock(Process.class);
    when(processService.recordUserSyncProcessStarted()).thenReturn(process);

    Throwable error = catchThrowable(() -> syncService.syncUsersIfRequired());

    assertThat(error).isEqualTo(expectedError);
    verify(processService).recordProcessComplete(process);
  }

  @Test
  void shouldSetUserInactiveIfPresentInDatabaseAndActiveAndNotPresentInCognito() {
    when(runPolicy.shouldRun()).thenReturn(true);
    when(userService.getCognitoUsers()).thenReturn(Collections.emptyMap());
    User user = new User();
    user.setIdpUserId(SUBJECT);
    user.setIsActive(true);
    when(userRepository.findAll()).thenReturn(List.of(user));

    syncService.syncUsersIfRequired();

    verify(userRepository).saveAll(userCaptor.capture());
    Iterable<User> savedUsers = userCaptor.getValue();
    assertThat(savedUsers).map(User::getIsActive).containsExactly(false);
  }

  @Test
  void shouldCreateUserIfPresentInCognitoAndNotPresentInDatabase() {
    when(runPolicy.shouldRun()).thenReturn(true);
    CognitoUser user = cognitoUser();
    when(userService.getCognitoUsers()).thenReturn(Map.of(user.getSub(), user));
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    syncService.syncUsersIfRequired();

    InOrder inOrder = inOrder(userRepository);
    inOrder.verify(userRepository).findAll();
    inOrder.verify(userRepository).saveAll(userCaptor.capture());
    Collection<User> savedUsers = userCaptor.getValue();
    assertThat(savedUsers).hasSize(1);
    User savedUser = IterableUtils.get(savedUsers, 0);
    assertThat(savedUser.getId()).isNull();
    assertThat(savedUser.getUserName()).isEqualTo(user.getUserName());
    assertThat(savedUser.getEmailAddress()).isEqualTo(user.getEmailAddress());
    assertThat(savedUser.getIsActive()).isTrue();
    assertThat(savedUser.getIdpUserId()).isEqualTo(user.getSub());
    assertThat(savedUser.getGivenName()).isEqualTo(user.getGivenName());
    assertThat(savedUser.getFamilyName()).isEqualTo(user.getFamilyName());
    assertThat(savedUser.getEmailVerified()).isEqualTo(user.getEmailVerified());
    assertThat(savedUser.getGroups()).containsExactlyInAnyOrderElementsOf(user.getGroups());
    assertThat(savedUser.getAccount()).isNull();
  }

  @Test
  void shouldUpdateUserIfPresentInCognitoAndPresentInDatabase() {
    when(runPolicy.shouldRun()).thenReturn(true);
    CognitoUser cognitoUser = cognitoUser();
    when(userService.getCognitoUsers()).thenReturn(Map.of(cognitoUser.getSub(), cognitoUser));
    User dbUser = dbUser();
    when(userRepository.findAll()).thenReturn(List.of(dbUser));

    syncService.syncUsersIfRequired();

    verify(userRepository).saveAll(userCaptor.capture());
    Collection<User> savedUsers = userCaptor.getValue();
    assertThat(savedUsers).hasSize(1);
    User savedUser = IterableUtils.get(savedUsers, 0);
    assertThat(savedUser.getId()).isEqualTo(dbUser.getId());
    assertThat(savedUser.getUserName()).isEqualTo(cognitoUser.getUserName());
    assertThat(savedUser.getEmailAddress()).isEqualTo(cognitoUser.getEmailAddress());
    assertThat(savedUser.getIsActive()).isTrue();
    assertThat(savedUser.getIdpUserId()).isEqualTo(cognitoUser.getSub());
    assertThat(savedUser.getGivenName()).isEqualTo(cognitoUser.getGivenName());
    assertThat(savedUser.getFamilyName()).isEqualTo(cognitoUser.getFamilyName());
    assertThat(savedUser.getEmailVerified()).isEqualTo(cognitoUser.getEmailVerified());
    assertThat(savedUser.getGroups()).containsExactlyInAnyOrderElementsOf(cognitoUser.getGroups());
    assertThat(savedUser.getAccount()).isEqualTo(dbUser.getAccount());
  }

  private static User dbUser() {
    User user = new User();
    user.setIdpUserId(SUBJECT);
    user.setUserName("db_user");
    user.setEmailAddress("db_user@example.com");
    user.setIsActive(false);
    user.setGivenName("DB");
    user.setFamilyName("DB-User");
    user.setEmailVerified(false);
    user.setGroups(List.of("d-group"));
    user.setAccount(new Account());
    return user;
  }

  private static CognitoUser cognitoUser() {
    return CognitoUser.builder()
        .sub(SUBJECT)
        .userName("cognito_user")
        .emailAddress("cognito_user@example.com")
        .enabled(true)
        .givenName("Cognito")
        .familyName("COGNITO-User")
        .emailVerified(true)
        .groups(Set.of("c-group"))
        .build();
  }
}
