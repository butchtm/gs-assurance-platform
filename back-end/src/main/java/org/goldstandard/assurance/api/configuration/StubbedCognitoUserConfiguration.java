package org.goldstandard.assurance.api.configuration;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.dto.cognito.CognitoUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class StubbedCognitoUserConfiguration {

  @Value("${stubbed.users.gs-admin.subs}")
  private String gsAdminSubs;

  @Value("${stubbed.users.project-developer.subs}")
  private String pdSubs;

  @Value("${stubbed.users.arm-team.subs}")
  private String armTeamSubs;

  @Value("${stubbed.users.gs-registry.subs}")
  private String gsRegistrySubs;

  @Value("${stubbed.users.gs-finance.subs}")
  private String financeSubs;

  @Value("${stubbed.users.gs-reviewer.subs}")
  private String gsReviewerSubs;

  @Value("${stubbed.users.vvb.subs}")
  private String vvbSubs;

  @Value("${stubbed.users.tac.subs}")
  private String tacSubs;

  @Value("${stubbed.users.support.subs}")
  private String supportSubs;

  public Map<UUID, CognitoUser> getStubbedUsers() {
    return Stream.of(
            toUsers(gsAdminSubs, StubbedCognitoUserConfiguration::admin),
            toUsers(pdSubs, StubbedCognitoUserConfiguration::projectDeveloper),
            toUsers(armTeamSubs, StubbedCognitoUserConfiguration::armTeam),
            toUsers(gsRegistrySubs, StubbedCognitoUserConfiguration::gsRegistry),
            toUsers(financeSubs, StubbedCognitoUserConfiguration::finance),
            toUsers(gsReviewerSubs, StubbedCognitoUserConfiguration::gsReviewer),
            toUsers(vvbSubs, StubbedCognitoUserConfiguration::vvb),
            toUsers(tacSubs, StubbedCognitoUserConfiguration::tac),
            toUsers(supportSubs, StubbedCognitoUserConfiguration::support))
        .flatMap(Function.identity())
        .collect(Collectors.toMap(CognitoUser::getSub, Function.identity()));
  }

  private static CognitoUser admin(String sub, int index) {
    return toUser(ADMIN_ROLE, sub, index);
  }

  private static CognitoUser projectDeveloper(String sub, int index) {
    return toUser(PROJECT_DEVELOPER_ROLE, sub, index);
  }

  private static CognitoUser armTeam(String sub, int index) {
    return toUser(ARM_TEAM_ROLE, sub, index);
  }

  private static CognitoUser gsRegistry(String sub, int index) {
    return toUser(GS_REGISTRY_ROLE, sub, index);
  }

  private static CognitoUser finance(String sub, int index) {
    return toUser(FINANCE_ROLE, sub, index);
  }

  private static CognitoUser gsReviewer(String sub, int index) {
    return toUser(GS_REVIEWER_ROLE, sub, index);
  }

  private static CognitoUser vvb(String sub, int index) {
    return toUser(VVB_ROLE, sub, index);
  }

  private static CognitoUser tac(String sub, int index) {
    return toUser(TAC_ROLE, sub, index);
  }

  private static CognitoUser support(String sub, int index) {
    return toUser(SUPPORT_ROLE, sub, index);
  }

  private static Stream<CognitoUser> toUsers(
      String subs, BiFunction<String, Integer, CognitoUser> toUser) {
    String[] subsArray = subs.split(",");
    return IntStream.range(0, subsArray.length).mapToObj(i -> toUser.apply(subsArray[i], i));
  }

  private static CognitoUser toUser(String role, String sub, int index) {
    String username = toStubbedUsername(role, index);
    return CognitoUser.builder()
        .sub(UUID.fromString(sub))
        .userName(username)
        .emailAddress(toStubbedEmail(username))
        .enabled(true)
        .givenName("John")
        .familyName(toStubbedFamilyName(role, index))
        .emailVerified(true)
        .groups(Set.of(role))
        .build();
  }

  private static String toStubbedUsername(String role, int index) {
    return String.format("john-stubbed-%s-%d", role.toLowerCase(), index);
  }

  private static String toStubbedFamilyName(String role, int index) {
    return String.format("Stubbed-%s-%d", role.replace("-", ""), index);
  }

  private static String toStubbedEmail(String username) {
    return String.format("%s@caci.co.uk", username);
  }
}
