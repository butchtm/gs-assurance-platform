package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.populator.AccountPopulator.ARM_ACCOUNT_ID;
import static org.goldstandard.assurance.api.populator.AccountPopulator.PROJECT_DEVELOPER_ACCOUNT_ID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserPopulator {

  public static final UUID NO_ACCOUNT_USER_ID1 =
      UUID.fromString("c1071d2b-c245-40e5-b6ae-dfb84a8e9f61");
  public static final UUID NO_ACCOUNT_USER_ID2 =
      UUID.fromString("54711e1f-f332-4f31-9bc9-a2f43539e73f");
  public static final UUID ASSIGNED_ACCOUNT1_USER_ID =
      UUID.fromString("7b61d652-70d5-4ab6-a8f0-0bd177686b27");
  public static final UUID ASSIGNED_REQUEST_USER_ID = ASSIGNED_ACCOUNT1_USER_ID;
  public static final UUID ANY_USER_ID = ASSIGNED_ACCOUNT1_USER_ID;
  public static final UUID VVB_ACCOUNT_USER_ID = ASSIGNED_ACCOUNT1_USER_ID;
  public static final UUID ASSIGNED_ACCOUNT2_USER_ID =
      UUID.fromString("ce0985e3-3436-44be-b58f-c6a84ea0699e");
  public static final UUID NON_VVB_ACCOUNT_USER_ID = ASSIGNED_ACCOUNT2_USER_ID;
  public static final UUID ARM_USER_ID = UUID.fromString("3ca98ff3-ab54-4435-ab51-ef3ae6ee6340");
  public static final UUID PROJECT_DEVELOPER_USER_ID =
      UUID.fromString("4ba98ff3-ab54-4435-ab51-ef3ae6ee6340");

  private static final List<UUID> USER_IDS =
      List.of(
          NO_ACCOUNT_USER_ID1,
          NO_ACCOUNT_USER_ID2,
          UUID.fromString("4cfab1e9-2fa8-44b6-8366-613b47160fbf"),
          UUID.fromString("f5bf3fa7-65ee-4a28-9f47-5ba1a3065bab"),
          ASSIGNED_ACCOUNT1_USER_ID,
          ASSIGNED_ACCOUNT2_USER_ID);

  @Autowired private UserRepository userRepository;

  @Autowired private AccountRepository accountRepository;

  public boolean isPopulated() {
    return userRepository.count() > 0;
  }

  public void populate() {
    List<User> savedUsers = userRepository.saveAll(buildUsers());
    logUserAccounts(savedUsers);
  }

  public int getNumberOfUsers() {
    return Math.toIntExact(userRepository.count());
  }

  public int getNumberOfActiveUsers() {
    return Math.toIntExact(userRepository.countByIsActive(true));
  }

  public List<User> getUsers() {
    return userRepository.findAll(Sort.by(Sort.Order.asc("id")));
  }

  public List<UUID> getUserIds() {
    return getUsers().stream().map(User::getIdpUserId).toList();
  }

  public int getNumberOfUsersInAccount(Integer accountId) {
    return userRepository.findAllByAccountId(accountId).size();
  }

  public User getUserByIdpUserId(UUID id) {
    return userRepository.findByIdpUserId(id).orElseThrow();
  }

  private void logUserAccounts(List<User> users) {
    users.forEach(this::logUserAccount);
  }

  private void logUserAccount(User user) {
    Optional<Integer> accountId = Optional.ofNullable(user.getAccount()).map(Account::getId);
    log.info(
        "populated user {} idp id {} with account {}",
        user.getId(),
        user.getIdpUserId(),
        accountId);
  }

  private List<User> buildUsers() {
    return IntStream.range(0, 8).map(i -> i + 1).mapToObj(this::buildUser).toList();
  }

  private User buildUser(int i) {
    if (i == 7) {
      return buildArmUser();
    }
    if (i == 8) {
      return buildProjectDeveloperUser();
    }
    var user = new User();
    user.setUserName("User " + i);
    user.setGivenName("Test");
    user.setFamilyName(Integer.toString(i));
    user.setEmailAddress("user_" + i + "@test");
    user.setIsActive(i % 2 == 0);
    user.setIdpUserId(USER_IDS.get(i - 1));
    findAccount(i).ifPresent(user::setAccount);
    return user;
  }

  private static User buildArmUser() {
    var user = new User();
    user.setUserName("test-arm");
    user.setGivenName("Test");
    user.setFamilyName("ARM");
    user.setEmailAddress("arm@test");
    user.setIsActive(true);
    user.setAccount(buildArmAccount());
    user.setIdpUserId(ARM_USER_ID);
    return user;
  }

  private static User buildProjectDeveloperUser() {
    var user = new User();
    user.setUserName("test-project-developer");
    user.setGivenName("Test");
    user.setFamilyName("PD");
    user.setEmailAddress("project-developer@test");
    user.setIsActive(true);
    user.setAccount(buildProjectDeveloperAccount());
    user.setIdpUserId(PROJECT_DEVELOPER_USER_ID);
    return user;
  }

  private Optional<Account> findAccount(int i) {
    // The first 3 Users will not be associated with an account because they are needed for specific
    // tests
    if (i <= 3) {
      return Optional.empty();
    }
    int accountId = (i - 2) >> 1;
    return accountRepository.findById(accountId);
  }

  private static Account buildArmAccount() {
    var account = new Account();
    account.setId(ARM_ACCOUNT_ID);
    return account;
  }

  private static Account buildProjectDeveloperAccount() {
    var account = new Account();
    account.setId(PROJECT_DEVELOPER_ACCOUNT_ID);
    return account;
  }
}
