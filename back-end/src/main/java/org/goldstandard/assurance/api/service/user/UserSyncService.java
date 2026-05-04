package org.goldstandard.assurance.api.service.user;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.configuration.UserSyncConfiguration;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.system.Process;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.process.ProcessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Builder
public class UserSyncService {

  private final ProcessService processService;
  private final UserRepository userRepository;
  private final UserSyncConfiguration config;
  private final UserSyncRunPolicy runPolicy;
  private final CognitoUserFinder userService;

  @Scheduled(cron = "${user-sync.check-cron}")
  public void syncUsersIfRequired() {
    if (!runPolicy.shouldRun()) {
      log.debug("User sync not required so not running");
      return;
    }
    syncUsers();
  }

  public void syncUsers() {
    Process process = processService.recordUserSyncProcessStarted();
    try {
      Map<UUID, User> dbUsers = getAllDatabaseUsers();
      Map<UUID, CognitoUser> cognitoUsers = userService.getCognitoUsers();
      upsertCognitoUsers(dbUsers, cognitoUsers);
      deactivateActiveUsersRemovedFromCognito(dbUsers, cognitoUsers);
      userRepository.saveAll(dbUsers.values());
    } finally {
      processService.recordProcessComplete(process);
    }
  }

  private Map<UUID, User> getAllDatabaseUsers() {
    return userRepository.findAll().stream()
        .collect(Collectors.toMap(User::getIdpUserId, Function.identity()));
  }

  private void upsertCognitoUsers(Map<UUID, User> dbUsers, Map<UUID, CognitoUser> cognitoUsers) {
    for (CognitoUser cognitoUser : cognitoUsers.values()) {
      User dbUser = dbUsers.getOrDefault(cognitoUser.getSub(), new User());
      updateUser(dbUser, cognitoUser);
      dbUsers.put(dbUser.getIdpUserId(), dbUser);
    }
  }

  private void updateUser(User dbUser, CognitoUser cognitoUser) {
    dbUser.setIdpUserId(cognitoUser.getSub());
    dbUser.setUserName(cognitoUser.getUserName());
    dbUser.setEmailAddress(cognitoUser.getEmailAddress());
    dbUser.setIsActive(cognitoUser.isEnabled());
    dbUser.setGivenName(cognitoUser.getGivenName());
    dbUser.setFamilyName(cognitoUser.getFamilyName());
    dbUser.setEmailVerified(cognitoUser.getEmailVerified());
    dbUser.setGroups(cognitoUser.getGroups().stream().toList());
  }

  private void deactivateActiveUsersRemovedFromCognito(
      Map<UUID, User> dbUsers, Map<UUID, CognitoUser> cognitoUsers) {
    dbUsers.values().stream()
        .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
        .filter(u -> !cognitoUsers.containsKey(u.getIdpUserId()))
        .forEach(this::deactivateUser);
  }

  private void deactivateUser(User user) {
    log.info("User with subject {} not in user pool, deactivating user", user.getIdpUserId());
    user.setIsActive(false);
  }
}
