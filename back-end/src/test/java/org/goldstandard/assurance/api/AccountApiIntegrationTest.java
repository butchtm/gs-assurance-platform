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
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.AuditEvent;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class AccountApiIntegrationTest extends AbstractApiTest {

  private static final String ACCOUNTS_ENDPOINT = "/accounts";

  private static final Integer TEST_ACCOUNT_ID = 1;

  @Autowired private AccountRepository accountRepository;

  @Autowired private AuditRepository auditRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private UserPopulator userPopulator;
  @Autowired JsonConverter jsonConverter;

  @Test
  void testGetAccountsOld() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);

    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<AccountDTO>>() {});

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
        "?name=T 6",
        "?email=6@TEST",
        "?name=T 6&email=6@TEST",
        "?name=T 6&email=6@TEST&active=true"
      })
  void testGetAccountsReturnsFilteredResults(String query) {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + query,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<AccountDTO>>() {});

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
        "?name=T 99",
        "?email=99@TEST",
        "?name=T 11&email=99@TEST",
        "?name=T 11&email=99@TEST&active=false"
      })
  void testGetAccountsReturnsNoResultsForInvalidFilters(String query) {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + query,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<AccountDTO>>() {});

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
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/%s?%s", baseUrl, ACCOUNTS_ENDPOINT, query);
    var response =
        this.restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<AccountDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var accounts = response.getBody().getContent();
    assertThat(accounts).isNotNull();
    var accountIds = accounts.stream().map(AccountDTO::getId).collect(Collectors.toSet());
    assertThat(accountIds).isEqualTo(expectedAccountIds);
  }

  @Test
  void testGetAccountsUnauthenticatedIsDenied() {
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT, HttpMethod.GET, unauthenticated(), String.class);

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
    var requestEntity = this.getHttpEntityWithTokenForRole(testCase.getRole());
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PagedResource<AccountDTO>>() {});
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
    // Create Account
    var request = createValidAccountCreateUpdate();

    var requestEntity = getHttpEntityWithTokenAndBodyForRole(request, ADMIN_ROLE);

    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT, HttpMethod.POST, requestEntity, Integer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    // Just verify it is a number like we expect
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isPositive();

    // Verify it is stored as we expect
    var savedEntityOptional = accountRepository.findById(response.getBody());
    assertTrue(savedEntityOptional.isPresent());
    var savedEntity = savedEntityOptional.get();
    assertThat(savedEntity.getId()).isEqualTo(response.getBody());
    assertThat(savedEntity.getName()).isEqualTo("Test account");
    assertThat(savedEntity.getEmailAddress()).isEqualTo("Test email address");
    assertTrue(savedEntity.getIsActive());
    assertThat(savedEntity.getCreatedTime()).isNotNull();
    assertThat(savedEntity.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_1");
    assertThat(savedEntity.getAddress()).isEqualTo("A\n long\n address");

    // Clean up
    // We have to update all the users to remove their account reference first
    var usersWithAccountRemoved = userRepository.findAllById(request.getUserIds());
    usersWithAccountRemoved.forEach(user -> user.setAccount(null));
    userRepository.saveAll(usersWithAccountRemoved);
    // Now we can safely delete the account
    accountRepository.deleteById(response.getBody());

    // Verify audit event was created
    List<AuditEvent> auditEvents =
        auditRepository.findAllByEventSubject(AuditEventSubject.ACCOUNT_CREATE.name());
    assertThat(auditEvents).hasSize(1);
    // The audit event should store the accountId of the account that was created
    assertThat(jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation()))
        .containsEntry("accountId", response.getBody());
  }

  @Test
  void testCreateValidAccountNotAdminForbidden() {
    // Create Account
    var request = createValidAccountCreateUpdate();

    var requestEntity = getHttpEntityWithTokenAndBody(request);

    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT, HttpMethod.POST, requestEntity, String.class);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void testCreateAccountsUnauthenticatedIsDenied() {
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT, HttpMethod.POST, unauthenticated(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetAccountById() {
    var requestEntity = this.getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + "/1",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<AccountDTO>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var account = response.getBody();

    assertThat(account.getId()).isEqualTo(1);
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
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + "/1", HttpMethod.GET, unauthenticated(), String.class);

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
    var requestEntity = this.getHttpEntityWithTokenForRoleWithId(testCase.getRole(), userId);
    var response =
        this.restTemplate.exchange(
            String.format("%s%s/%d", baseUrl, ACCOUNTS_ENDPOINT, TEST_ACCOUNT_ID),
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<AccountDTO>() {});
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
    var requestEntity = this.getHttpEntityWithToken();
    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + "/1", HttpMethod.GET, requestEntity, String.class);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void testUpdateAccount() {
    // Create Account in Database
    var accountEntity = new Account();
    accountEntity.setName("Test account");
    accountEntity.setAddress("Test address");
    accountEntity.setEmailAddress("Test email address");
    accountEntity.setAccountType(new AccountType(1));
    accountEntity.setCreatedTime(FIXED_TIME);
    accountEntity.setIsActive(true);
    var createdEntity = accountRepository.save(accountEntity);

    // An account update request
    var request = createValidAccountUpdate();

    var requestEntity = getHttpEntityWithTokenAndBodyForRole(request, ADMIN_ROLE);

    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + "/" + createdEntity.getId(),
            HttpMethod.PUT,
            requestEntity,
            Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // Retrieve the database record to check it was updated
    var savedEntityOptional = accountRepository.findById(createdEntity.getId());
    assertTrue(savedEntityOptional.isPresent());
    var savedEntity = savedEntityOptional.get();
    assertFalse(savedEntity.getIsActive());
    assertThat(savedEntity.getAddress()).isEqualTo("Test address 2");
    assertThat(savedEntity.getAccountType().getCode()).isEqualTo("ACCOUNT_TYPE_2");
    assertThat(savedEntity.getCreatedTime().toEpochSecond()).isEqualTo(FIXED_TIME.toEpochSecond());
    assertThat(savedEntity.getEmailAddress()).isEqualTo("Test email address 2");
    assertThat(savedEntity.getName()).isEqualTo("Test account 2");

    // Clean up
    accountRepository.deleteById(createdEntity.getId());

    // Verify audit event was created
    List<AuditEvent> auditEvents =
        auditRepository.findAllByEventSubject(AuditEventSubject.ACCOUNT_UPDATE.name());
    assertThat(auditEvents).hasSize(1);
    // The audit event should store the accountId of the account that was updated
    assertThat(jsonConverter.toMap(auditEvents.getFirst().getAdditionalInformation()))
        .containsEntry("accountId", createdEntity.getId());
  }

  @Test
  void testUpdateAccountNotAdminForbidden() {
    var request = createValidAccountUpdate();

    var requestEntity = getHttpEntityWithTokenAndBody(request);

    var response =
        this.restTemplate.exchange(
            baseUrl + ACCOUNTS_ENDPOINT + "/" + 1, HttpMethod.PUT, requestEntity, Void.class);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
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
