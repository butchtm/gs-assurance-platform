package org.goldstandard.assurance.api.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.UpsertUserRequest;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.UserAlreadyExistsException;
import org.goldstandard.assurance.api.exception.UserUpsertException;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.user.CognitoUserService;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.goldstandard.assurance.api.service.user.UserUpserter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import software.amazon.awssdk.core.exception.SdkException;

@ExtendWith(MockitoExtension.class)
class UserUpserterTest {

  @InjectMocks UserUpserter upserter;

  @Mock UserFinder finder;

  @Mock UserRepository repository;

  @Mock CognitoUserService cognitoService;

  @Mock AccountFinder accountFinder;

  @Mock User user;

  @Mock Account account;

  @Test
  void shouldUpdateIfUserExistsInRepositoryAndCognito() {
    var request = buildRequest();

    when(finder.getByUserName(anyString())).thenReturn(Optional.of(user));
    when(cognitoService.exists(anyString())).thenReturn(true);

    upserter.upsert(request);

    verify(cognitoService).update(user);
    verify(repository).save(user);
  }

  @Test
  void shouldCreateIfUserDoesNoExistInRepository() {
    var request = buildRequest();

    when(finder.getByUserName(anyString())).thenReturn(Optional.empty());
    when(cognitoService.create(any())).thenReturn(user);

    upserter.upsert(request);

    verify(cognitoService).create(request);
    verify(repository).save(user);
  }

  @Test
  void shouldThrowExceptionIfCannotGetUserInCognito() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.of(user));
    when(cognitoService.exists(anyString())).thenReturn(false);

    var request = buildRequest();

    assertThrows(UserUpsertException.class, () -> upserter.upsert(request));

    verify(cognitoService, never()).update(any());
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCannotUpdateUserInCognito() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.of(user));
    when(cognitoService.exists(anyString())).thenReturn(true);
    doThrow(SdkException.create("could not update user", null)).when(cognitoService).update(any());

    assertThrows(UserUpsertException.class, () -> upserter.upsert(buildRequest()));

    verify(cognitoService).update(user);
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCannotCreateUserInCognito() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.empty());
    when(cognitoService.create(any()))
        .thenThrow(SdkException.create("could not create user", null));

    var request = buildRequest();

    assertThrows(UserUpsertException.class, () -> upserter.upsert(request));

    verify(cognitoService).create(request);
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCannotUpdateUserInRepository() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.of(user));
    when(cognitoService.exists(anyString())).thenReturn(true);
    when(repository.save(any())).thenThrow(new QueryTimeoutException("timeout"));

    assertThrows(UserUpsertException.class, () -> upserter.upsert(buildRequest()));

    verify(cognitoService).update(user);
  }

  @Test
  void shouldThrowExceptionIfCannotCreateUserInRepository() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.empty());
    when(cognitoService.create(any())).thenReturn(user);
    when(repository.save(any())).thenThrow(new QueryTimeoutException("timeout"));

    var request = buildRequest();
    assertThrows(UserUpsertException.class, () -> upserter.upsert(request));

    verify(cognitoService).create(request);
  }

  @Test
  void shouldThrowExceptionIfUserAlreadyExistsInCognito() {
    when(accountFinder.forceGetAccountById(anyInt())).thenReturn(account);
    when(finder.getByUserName(anyString())).thenReturn(Optional.empty());
    when(cognitoService.create(any())).thenThrow(new UserAlreadyExistsException("uname"));

    var request = buildRequest();
    assertThrows(UserUpsertException.class, () -> upserter.upsert(request));

    verify(cognitoService).create(request);
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCannotFindAccountWhenCreatingUser() {
    when(accountFinder.forceGetAccountById(any()))
        .thenThrow(new DataException("Account with id not found"));
    when(finder.getByUserName(anyString())).thenReturn(Optional.empty());

    assertThrows(UserUpsertException.class, () -> upserter.upsert(buildRequest()));

    verify(cognitoService, never()).update(any());
    verify(cognitoService, never()).create(any());
    verify(cognitoService, never()).getByUsername(any());
    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCannotFindAccountWhenUpdatingUser() {
    when(accountFinder.forceGetAccountById(any()))
        .thenThrow(new DataException("Account with id not found"));
    when(finder.getByUserName(anyString())).thenReturn(Optional.of(user));

    assertThrows(UserUpsertException.class, () -> upserter.upsert(buildRequest()));

    verify(cognitoService, never()).update(any());
    verify(cognitoService, never()).create(any());
    verify(cognitoService, never()).getByUsername(any());
    verify(repository, never()).save(any());
  }

  private UpsertUserRequest buildRequest() {
    return UpsertUserRequest.builder()
        .accountId(123)
        .userName("uname1")
        .email("email1")
        .firstName("fname1")
        .lastName("lname1")
        .groups(List.of("g1", "g2"))
        .build();
  }
}
