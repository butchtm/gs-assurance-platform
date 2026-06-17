package org.goldstandard.assurance.api.populator;

import static org.goldstandard.assurance.api.entity.lookup.AccountType.PROJECT_DEVELOPER_TYPE_ID;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.FIXED_DATE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodId;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.AccountType;
import org.goldstandard.assurance.api.entity.lookup.CarbonStream;
import org.goldstandard.assurance.api.entity.lookup.CurrentStandardVersion;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RegistrationType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.CurrentStandardVersionRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.ProjectSizeRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CrossLinkingProjectFixture {

  private static final int CURRENT_STANDARD_VERSION_ID = 2;
  private static final int CARBON_STREAM_ID = 1;
  private static final int PROJECT_SIZE_ID = 1;
  private static final int REGISTRATION_TYPE_ID = 0;
  private static final int FEE_SCHEDULE_ID = 0;
  private static final int PROJECT_TYPE_ID = 4;
  private static final int PROJECT_ASSET_ID = 0;
  private static final int PROJECT_LOCATION_ID = 2;

  public static final UUID ALPHA_PD_USER_ID =
      UUID.fromString("b1131111-1111-4111-8111-111111111111");
  public static final UUID BETA_PD_USER_ID =
      UUID.fromString("b1132222-2222-4222-8222-222222222222");

  public static final String ALPHA_ACCOUNT_NAME = "Cross Link Alpha PD";
  public static final String BETA_ACCOUNT_NAME = "Cross Link Beta PD";

  public static final String BETA_NEW_POA_NAME = "Cross Link Beta PoA - New";
  public static final String BETA_LISTED_POA_NAME = "Cross Link Beta PoA - Listed";
  public static final String BETA_DESIGN_CERTIFIED_POA_NAME =
      "Cross Link Beta PoA - Design Certified";
  public static final String BETA_REAL_CASE_ACM0001_NAME = "Cross Link Beta Real Case - ACM0001";
  public static final String BETA_REAL_CASE_ACM0002_NAME = "Cross Link Beta Real Case - ACM0002";
  public static final ZonedDateTime ACM0001_CREDITING_PERIOD_START =
      ZonedDateTime.parse("2024-05-20T00:00:00Z");
  public static final ZonedDateTime ACM0001_CREDITING_PERIOD_END =
      ZonedDateTime.parse("2025-05-20T00:00:00Z");
  public static final ZonedDateTime ACM0002_CREDITING_PERIOD_START =
      ZonedDateTime.parse("2024-06-20T00:00:00Z");
  public static final ZonedDateTime ACM0002_CREDITING_PERIOD_END =
      ZonedDateTime.parse("2025-06-20T00:00:00Z");

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;
  private final ProjectCreditingPeriodRepository creditingPeriodRepository;
  private final CurrentStandardVersionRepository currentStandardVersionRepository;
  private final ProjectSizeRepository projectSizeRepository;

  public CrossLinkingProjectFixture(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ProjectRepository projectRepository,
      ProjectCreditingPeriodRepository creditingPeriodRepository,
      CurrentStandardVersionRepository currentStandardVersionRepository,
      ProjectSizeRepository projectSizeRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
    this.creditingPeriodRepository = creditingPeriodRepository;
    this.currentStandardVersionRepository = currentStandardVersionRepository;
    this.projectSizeRepository = projectSizeRepository;
  }

  @Transactional
  public CrossLinkingProjects populate() {
    var alphaAccount = upsertAlphaAccount();
    var betaAccount = upsertBetaAccount();
    var alphaUser = upsertAlphaUser(alphaAccount);
    var betaUser = upsertBetaUser(betaAccount);

    var betaNewPoa = upsertPoa(betaAccount, BETA_NEW_POA_NAME, ProjectStatus.NEW, 0, 1, 1000);
    var betaListedPoa =
        upsertPoa(betaAccount, BETA_LISTED_POA_NAME, ProjectStatus.LISTED, 0, 1, 2000);
    var betaDesignCertifiedPoa =
        upsertPoa(
            betaAccount,
            BETA_DESIGN_CERTIFIED_POA_NAME,
            ProjectStatus.DESIGN_CERTIFIED,
            1,
            2,
            3000);
    var betaRealCaseAcm0001 =
        upsertRealCase(
            betaAccount,
            betaListedPoa,
            BETA_REAL_CASE_ACM0001_NAME,
            ProjectStatus.LISTED,
            0,
            1,
            2100,
            ACM0001_CREDITING_PERIOD_START,
            ACM0001_CREDITING_PERIOD_END,
            1.0);
    var betaRealCaseAcm0002 =
        upsertRealCase(
            betaAccount,
            betaDesignCertifiedPoa,
            BETA_REAL_CASE_ACM0002_NAME,
            ProjectStatus.DESIGN_CERTIFIED,
            1,
            2,
            3100,
            ACM0002_CREDITING_PERIOD_START,
            ACM0002_CREDITING_PERIOD_END,
            2.0);

    return new CrossLinkingProjects(
        alphaAccount,
        alphaUser,
        betaAccount,
        betaUser,
        betaNewPoa,
        betaListedPoa,
        betaDesignCertifiedPoa,
        betaRealCaseAcm0001,
        betaRealCaseAcm0002);
  }

  private Optional<Account> findAccountByName(String name) {
    return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
        .filter(account -> name.equals(account.getName()))
        .findFirst();
  }

  private Optional<Project> findProjectByAccountAndName(Account account, String name) {
    return projectRepository.findAllByAccountId(account.getId()).stream()
        .filter(project -> name.equals(project.getName()))
        .findFirst();
  }

  private Account upsertAlphaAccount() {
    return upsertAccount(ALPHA_ACCOUNT_NAME, "cross-link-alpha-pd@test");
  }

  private Account upsertBetaAccount() {
    return upsertAccount(BETA_ACCOUNT_NAME, "cross-link-beta-pd@test");
  }

  private Account upsertAccount(String name, String emailAddress) {
    var account = findAccountByName(name).orElseGet(Account::new);
    account.setName(name);
    account.setEmailAddress(emailAddress);
    account.setAddress(name + " address");
    account.setCreatedTime(FIXED_DATE);
    account.setIsActive(true);
    account.setAccountType(new AccountType(PROJECT_DEVELOPER_TYPE_ID));
    return accountRepository.save(account);
  }

  private User upsertAlphaUser(Account account) {
    return upsertUser(
        "cross-link-alpha-pd",
        "Alpha",
        "PD",
        "cross-link-alpha-pd@test",
        ALPHA_PD_USER_ID,
        account);
  }

  private User upsertBetaUser(Account account) {
    return upsertUser(
        "cross-link-beta-pd", "Beta", "PD", "cross-link-beta-pd@test", BETA_PD_USER_ID, account);
  }

  private User upsertUser(
      String userName,
      String givenName,
      String familyName,
      String emailAddress,
      UUID idpUserId,
      Account account) {
    var user = userRepository.findByIdpUserId(idpUserId).orElseGet(User::new);
    user.setUserName(userName);
    user.setGivenName(givenName);
    user.setFamilyName(familyName);
    user.setEmailAddress(emailAddress);
    user.setIdpUserId(idpUserId);
    user.setAccount(account);
    user.setIsActive(true);
    user.setEmailVerified(true);
    user.setGroups(new ArrayList<>(List.of(PROJECT_DEVELOPER_ROLE)));
    return userRepository.save(user);
  }

  private Project upsertPoa(
      Account account,
      String name,
      int statusId,
      int methodologyId,
      int sustainableDevelopmentGoalId,
      int estimatedAnnualUnits) {
    return upsertProject(
        account,
        name,
        new ProjectClassification(ProjectPopulator.PROGRAMME_OF_ACTIVITIES_ID),
        new ProjectStatus(statusId),
        null,
        methodologyId,
        sustainableDevelopmentGoalId,
        estimatedAnnualUnits);
  }

  private Project upsertRealCase(
      Account account,
      Project poa,
      String name,
      int statusId,
      int methodologyId,
      int sustainableDevelopmentGoalId,
      int estimatedAnnualUnits,
      ZonedDateTime creditingPeriodStart,
      ZonedDateTime creditingPeriodEnd,
      double creditingPeriodMethodologyVersion) {
    var project =
        upsertProject(
            account,
            name,
            new ProjectClassification(ProjectPopulator.REAL_CASE_ID),
            poa,
            new ProjectStatus(statusId),
            methodologyId,
            sustainableDevelopmentGoalId,
            estimatedAnnualUnits);
    resetCreditingPeriod(
        project,
        methodologyId,
        creditingPeriodStart,
        creditingPeriodEnd,
        creditingPeriodMethodologyVersion);
    return project;
  }

  private Project upsertProject(
      Account account,
      String name,
      ProjectClassification classification,
      ProjectStatus status,
      Project masterProject,
      int methodologyId,
      int sustainableDevelopmentGoalId,
      int estimatedAnnualUnits) {
    var project = findProjectByAccountAndName(account, name).orElseGet(Project::new);
    project.setName(name);
    project.setDescription(name + " fixture");
    project.setStatus(false);
    project.setProjectClassification(classification);
    project.setProjectStartDate(FIXED_DATE);
    project.setRegistrationType(new RegistrationType(REGISTRATION_TYPE_ID));
    project.setHasProjectAssets(true);
    project.setCarbonStream(new CarbonStream(CARBON_STREAM_ID));
    project.setCurrentStandardVersion(getCurrentStandardVersion());
    project.setProjectSize(getProjectSize());
    project.setProjectStatus(status);
    project.setDesignCertificationDateManualSet(false);
    project.setListingDateManualSet(false);
    project.setFeeSchedule(new FeeSchedule(FEE_SCHEDULE_ID));
    project.setEstimatedAnnualUnits(estimatedAnnualUnits);
    project.setAccountId(account.getId());
    project.setMasterProject(masterProject);
    project.setParentProject(masterProject);
    project.setProjectTypes(new ArrayList<>(List.of(new ProjectType(PROJECT_TYPE_ID))));
    project.setProjectAssets(new ArrayList<>(List.of(new ProjectAsset(PROJECT_ASSET_ID))));
    project.setProjectLocations(new ArrayList<>(List.of(new ProjectLocation(PROJECT_LOCATION_ID))));
    project.setSustainableDevelopmentGoals(
        new ArrayList<>(List.of(new SustainableDevelopmentGoal(sustainableDevelopmentGoalId))));
    project.setMethodologies(new ArrayList<>(List.of(new Methodology(methodologyId))));
    project.setLastActivity(FIXED_DATE);

    var savedProject = projectRepository.save(project);
    creditingPeriodRepository.deleteAllByProjectId(savedProject.getId());
    return savedProject;
  }

  private Project upsertProject(
      Account account,
      String name,
      ProjectClassification classification,
      Project parentProject,
      ProjectStatus status,
      int methodologyId,
      int sustainableDevelopmentGoalId,
      int estimatedAnnualUnits) {
    return upsertProject(
        account,
        name,
        classification,
        status,
        parentProject,
        methodologyId,
        sustainableDevelopmentGoalId,
        estimatedAnnualUnits);
  }

  private CurrentStandardVersion getCurrentStandardVersion() {
    return currentStandardVersionRepository.findById(CURRENT_STANDARD_VERSION_ID).orElseThrow();
  }

  private ProjectSize getProjectSize() {
    return projectSizeRepository.findById(PROJECT_SIZE_ID).orElseThrow();
  }

  private void resetCreditingPeriod(
      Project project,
      int methodologyId,
      ZonedDateTime startDate,
      ZonedDateTime endDate,
      double methodologyVersion) {
    creditingPeriodRepository.deleteAllByProjectId(project.getId());
    var periodId =
        ProjectCreditingPeriodId.builder().projectId(project.getId()).periodId(1).build();
    var methodology = new Methodology(methodologyId);
    var period =
        ProjectCreditingPeriod.builder()
            .projectId(periodId.getProjectId())
            .periodId(periodId.getPeriodId())
            .startDate(startDate)
            .endDate(endDate)
            .methodologies(
                List.of(
                    ProjectCreditingPeriodMethodology.builder()
                        .projectId(periodId.getProjectId())
                        .periodId(periodId.getPeriodId())
                        .methodologyId(methodology.getId())
                        .methodology(methodology)
                        .version(methodologyVersion)
                        .build()))
            .build();
    creditingPeriodRepository.save(period);
  }

  public record CrossLinkingProjects(
      Account alphaAccount,
      User alphaUser,
      Account betaAccount,
      User betaUser,
      Project betaNewPoaWithoutRealCase,
      Project betaListedPoa,
      Project betaDesignCertifiedPoa,
      Project betaRealCaseAcm0001,
      Project betaRealCaseAcm0002) {}
}
