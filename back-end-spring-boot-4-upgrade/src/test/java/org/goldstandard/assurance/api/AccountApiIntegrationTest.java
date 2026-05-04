package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.AccountType.VVB_ACCOUNT_TYPE_ID;
import static org.goldstandard.assurance.api.populator.AccountPopulator.FIXED_TIME;
import static org.goldstandard.assurance.api.populator.AccountPopulator.TOTAL_ACCOUNTS;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT1_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT2_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.goldstandard.assurance.api.dto.AccountCreateUpdate;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.populator.UserPopulator;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionTemplate;

class AccountApiIntegrationTest extends AbstractApiTest {

  private static final Integer ACCOUNT_ID = 1;

  @Autowired ApiClient client;
  @Autowired AccountRepository accountRepository;
  @Autowired AuditRepository auditRepository;
  @Autowired UserRepository userRepository;
  @Autowired UserPopulator userPopulator;
  @Autowired JsonConverter jsonConverter;
  @Autowired TransactionTemplate transactionTemplate;

  @Test
  void testGetAccounts() {
    var response = client.getAccounts(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var accounts = response.getBody().getContent();
    assertThat(accounts).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(TOTAL_ACCOUNTS);

    var overview = accounts.getFirst();
    assertThat(overview.getId()).isEqualTo(1);
    assertThat(overview.getName()).isEqualTo("Account 1");
    assertThat(overview.getEmailAddress()).isEqualTo("account_1@test");
    assertThat(overview.getIsActive()).isFalse();
    assertThat(overview.getAddress()).isEqualTo("Address 1");
    assertThat(overview.getCreatedTime().toEpochSecond()).isEqualTo(FIXED_TIME.toEpochSecond());
    assertThat(overview.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_1");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "name=T 6",
        "email=6@TEST",
        "name=T 6&email=6@TEST",
        "name=T 6&email=6@TEST&active=true"
      })
  void testGetAccountsReturnsFilteredResults(String query) {
    var response = client.getAccounts(ADMIN_ROLE, query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var accounts = response.getBody().getContent();
    assertThat(accounts).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);

    var account = accounts.getFirst();
    assertThat(account.getId()).isEqualTo(6);
    assertThat(account.getName()).isEqualTo("Account 6");
    assertThat(account.getEmailAddress()).isEqualTo("account_6@test");
    assertThat(account.getIsActive()).isTrue();
    assertThat(account.getAddress()).isEqualTo("Address 6");
    assertThat(account.getCreatedTime().toEpochSecond()).isEqualTo(FIXED_TIME.toEpochSecond());
    assertThat(account.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_3");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "name=T 99",
        "email=99@TEST",
        "name=T 11&email=99@TEST",
        "name=T 11&email=99@TEST&active=false"
      })
  void testGetAccountsReturnsNoResultsForInvalidFilters(String query) {
    var response = client.getAccounts(ADMIN_ROLE, query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var accounts = response.getBody().getContent();
    assertThat(accounts).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
  }

  private static Stream<Arguments> accountFilterTestCases() {
    return Stream.of(
        Arguments.arguments("assignmentTypeId=2", Set.of(1, 2, 4, 5, 8)),
        Arguments.arguments("assignmentTypeId=2&name=T 2", Set.of(2)),
        Arguments.arguments("assignmentTypeId=2&active=true", Set.of(2, 4, 8)));
  }

  @ParameterizedTest
  @MethodSource("accountFilterTestCases")
  void testFilterAccountsOnAssignmentType(String query, Set<Integer> expectedAccountIds) {
    var response = client.getAccounts(ADMIN_ROLE, query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var accounts = response.getBody().getContent();
    assertThat(accounts).isNotNull();
    var accountIds = accounts.stream().map(AccountDTO::getId).collect(Collectors.toSet());
    assertThat(accountIds).isEqualTo(expectedAccountIds);
  }

  @Test
  void testGetAccountsUnauthenticatedIsDenied() {
    var response = client.getAccountsUnauthenticated();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Getter
  @AllArgsConstructor
  public enum GetAccountsTestCase {
    ADMIN_OMNIPOTENT(ADMIN_ROLE, null, false, HttpStatus.OK),
    ARM_OMNIPOTENT(ARM_TEAM_ROLE, null, false, HttpStatus.OK),
    PROJECT_DEVELOPER_SEE_VVBS_AND_NO_EMAIL(
        PROJECT_DEVELOPER_ROLE, Set.of(VVB_ACCOUNT_TYPE_ID), true, HttpStatus.OK),
    REVIEWER_FORBIDDEN(GS_REVIEWER_ROLE, null, null, HttpStatus.FORBIDDEN);
    private final String role;
    private final Set<Integer> expectedAccountTypes;
    private final Boolean expectedRestricted;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(GetAccountsTestCase.class)
  void testGetAccountsPermissionScopeAndRestrictedView(GetAccountsTestCase testCase) {
    var response = client.getAccounts(testCase.getRole());

    assertThat(response.getStatusCode()).isEqualTo(testCase.expectedStatus);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      var accounts = Objects.requireNonNull(response.getBody()).getContent();
      assertThat(accounts.stream().allMatch(a -> a.getEmailAddress() == null))
          .isEqualTo(testCase.expectedRestricted);
      assertThat(accounts.stream().allMatch(a -> a.getUsers().isEmpty()))
          .isEqualTo(testCase.expectedRestricted);
      if (testCase.expectedAccountTypes != null) {
        assertThat(
                accounts.stream().map(a -> a.getAccountType().getId()).collect(Collectors.toSet()))
            .isEqualTo(testCase.expectedAccountTypes);
      }
    }
  }

  @Test
  void testCreateValidAccount() {
    var dto = createValidAccountCreateUpdate();

    var response = client.createAccount(ADMIN_ROLE, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isPositive();
    try {
      var savedEntity = accountRepository.findById(response.getBody()).orElseThrow();
      assertThat(savedEntity.getId()).isEqualTo(response.getBody());
      assertThat(savedEntity.getName()).isEqualTo("Test account");
      assertThat(savedEntity.getEmailAddress()).isEqualTo("Test email address");
      assertTrue(savedEntity.getIsActive());
      assertThat(savedEntity.getCreatedTime()).isNotNull();
      assertThat(savedEntity.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_1");
      assertThat(savedEntity.getAddress()).isEqualTo("A\n long\n address");

      var auditEvents =
          auditRepository.findAllByEventSubject(AuditEventSubject.ACCOUNT_CREATE.name());
      assertThat(auditEvents).hasSize(1);
      var additionalInfo = auditEvents.getFirst().getAdditionalInformation();
      assertThat(jsonConverter.toMap(additionalInfo))
          .containsEntry("accountId", response.getBody());
    } finally {
      removeUserAccountsAndAccount(dto.getUserIds(), response.getBody());
    }
  }

  @Test
  void testCreateValidAccountNotAdminForbidden() {
    var dto = createValidAccountCreateUpdate();

    var response = client.createAccount(PROJECT_DEVELOPER_ROLE, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testCreateAccountsUnauthenticatedIsDenied() {
    var dto = createValidAccountCreateUpdate();

    var response = client.createAccountUnauthenticated(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetAccountById() {

    var response = client.getAccount(ADMIN_ROLE, ACCOUNT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var account = response.getBody();
    assertThat(account.getId()).isEqualTo(ACCOUNT_ID);
    assertThat(account.getName()).isEqualTo("Account 1");
    assertThat(account.getEmailAddress()).isEqualTo("account_1@test");
    assertThat(account.getIsActive()).isFalse();
    assertThat(account.getAddress()).isEqualTo("Address 1");
    assertThat(account.getCreatedTime().toEpochSecond()).isEqualTo(FIXED_TIME.toEpochSecond());
    assertThat(account.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_1");
    assertThat(account.getUsers()).hasSize(userPopulator.getNumberOfUsersInAccount(1));
  }

  @Test
  void testGetAccountUnauthenticatedIsDenied() {
    var response = client.getAccountUnauthenticated(ACCOUNT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Getter
  @AllArgsConstructor
  public enum GetAccountTestCase {
    ADMIN_OMNIPOTENT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, false, HttpStatus.OK),
    ARM_OMNIPOTENT(ARM_TEAM_ROLE, NO_ACCOUNT_USER_ID1, false, HttpStatus.OK),
    NON_ADMIN_OR_ARM_SEES_RESTRICTED_VIEW_OF_OWN_ACCOUNT(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT1_USER_ID, true, HttpStatus.OK),
    NON_ADMIN_OR_ARM_CANNOT_SEE_OTHER_ACCOUNT(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT2_USER_ID, null, HttpStatus.UNAUTHORIZED);
    private final String role;
    private final UUID userId;
    private final Boolean expectedRestricted;
    private final HttpStatus expectedStatus;
  }

  @ParameterizedTest
  @EnumSource(GetAccountTestCase.class)
  void testGetAccountPermissionScopeAndRestrictedView(GetAccountTestCase testCase) {
    var userId = testCase.getUserId();
    var user = userPopulator.getUserByIdpUserId(userId);

    var response = client.getAccount(testCase.getRole(), userId, ACCOUNT_ID);

    assertThat(response.getStatusCode()).isEqualTo(testCase.expectedStatus);
    if (response.getStatusCode().equals(HttpStatus.OK)) {
      var account = Objects.requireNonNull(response.getBody());
      // do not restrict authenticated user details
      assertThat(
              account.getUsers().stream()
                  .filter(u -> Objects.equals(u.getId(), user.getId()))
                  .allMatch(this::allUserFieldsRestricted))
          .isEqualTo(List.of(ADMIN_ROLE, ARM_TEAM_ROLE).contains(testCase.getRole()));
      // but restrict any other user details if expected
      assertThat(
              account.getUsers().stream()
                  .filter(u -> !Objects.equals(u.getId(), user.getId()))
                  .allMatch(this::allUserFieldsRestricted))
          .isEqualTo(testCase.getExpectedRestricted());
    }
  }

  @Test
  void testGetAccountByIdNotAdminForbidden() {
    var response = client.getAccount(null, ACCOUNT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testUpdateAccount() {
    var account = givenAccountExists();
    var dto = createValidAccountUpdate();

    var response = client.updateAccount(ADMIN_ROLE, account.getId(), dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    try {
      var savedEntity = accountRepository.findById(account.getId()).orElseThrow();
      assertFalse(savedEntity.getIsActive());
      assertThat(savedEntity.getAddress()).isEqualTo("Test address 2");
      assertThat(savedEntity.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_2");
      assertThat(savedEntity.getCreatedTime().toEpochSecond())
          .isEqualTo(FIXED_TIME.toEpochSecond());
      assertThat(savedEntity.getEmailAddress()).isEqualTo("Test email address 2");
      assertThat(savedEntity.getName()).isEqualTo("Test account 2");

      var auditEvents =
          auditRepository.findAllByEventSubject(AuditEventSubject.ACCOUNT_UPDATE.name());
      assertThat(auditEvents).hasSize(1);
      var additionalInfo = auditEvents.getFirst().getAdditionalInformation();
      assertThat(jsonConverter.toMap(additionalInfo)).containsEntry("accountId", account.getId());
    } finally {
      accountRepository.deleteById(account.getId());
    }
  }

  @Test
  void testUpdateAccountNotAdminForbidden() {
    var account = givenAccountExists();
    var dto = createValidAccountUpdate();

    var response = client.updateAccount(PROJECT_DEVELOPER_ROLE, account.getId(), dto);

    try {
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    } finally {
      accountRepository.deleteById(account.getId());
    }
  }

  private void removeUserAccountsAndAccount(Collection<Integer> userIds, Integer accountId) {
    transactionTemplate.executeWithoutResult(
        status -> {
          var users = userRepository.findAllById(userIds);
          users.forEach(user -> user.setAccount(null));
          userRepository.saveAll(users);
          accountRepository.deleteById(accountId);
        });
  }

  private Account givenAccountExists() {
    var account = new Account();
    account.setName("Test account");
    account.setAddress("Test address");
    account.setEmailAddress("Test email address");
    account.setAccountType(new AccountType(1));
    account.setCreatedTime(FIXED_TIME);
    account.setIsActive(true);
    return accountRepository.save(account);
  }

  private boolean allUserFieldsRestricted(UserDTO user) {
    return Objects.isNull(user.getEmailAddress())
        && Objects.isNull(user.getGivenName())
        && Objects.isNull(user.getFamilyName());
  }

  private static AccountCreateUpdate createValidAccountUpdate() {
    var request = new AccountCreateUpdate();
    request.setName("Test account 2");
    request.setAccountTypeId(2);
    request.setIsActive(false);
    request.setAddress("Test address 2");
    request.setEmailAddress("Test email address 2");
    request.setUserIds(Set.of());
    return request;
  }

  private static AccountCreateUpdate createValidAccountCreateUpdate() {
    var request = new AccountCreateUpdate();
    request.setName("Test account");
    request.setEmailAddress("Test email address");
    request.setIsActive(true);
    request.setAddress("A\n long\n address");
    request.setAccountTypeId(1);
    request.setUserIds(Set.of(1, 2, 3));
    return request;
  }
}
