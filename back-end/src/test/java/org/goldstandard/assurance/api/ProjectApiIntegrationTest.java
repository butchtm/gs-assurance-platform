package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ALPHA_PD_USER_ID;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.BETA_PD_USER_ID;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.FIXED_DATE;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.NUMBER_OF_PROJECTS;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.PROGRAMME_OF_ACTIVITIES_ID;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.REAL_CASE_ID;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_BUNDLE_ID;
import static org.goldstandard.assurance.api.populator.ProjectPopulator.TEST_PROJECTS;
import static org.goldstandard.assurance.api.populator.UserPopulator.ARM_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT1_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.ASSIGNED_ACCOUNT2_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.IterableUtils;
import org.awaitility.Awaitility;
import org.goldstandard.assurance.api.dto.BundleDTO;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.CreditingPeriodMethodologyDTO;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateParentProjectDetailsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.dto.MethodologyHistoryDTO;
import org.goldstandard.assurance.api.dto.MethodologyUpdateDTO;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.dto.ProjectCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectStatusUpdateDTO;
import org.goldstandard.assurance.api.dto.ProjectTreeNodeDTO;
import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.dto.TransitionProjectDetails;
import org.goldstandard.assurance.api.entity.AuditEvent;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingMonitoringPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodId;
import org.goldstandard.assurance.api.entity.creditingperiod.ProjectCreditingPeriodMethodology;
import org.goldstandard.assurance.api.entity.lookup.FeeSchedule;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RegistrationType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.enumeration.MethodologyOperation;
import org.goldstandard.assurance.api.enumeration.ProjectUpdateVerdict;
import org.goldstandard.assurance.api.fixtures.CreateBatchRequestDTOMother;
import org.goldstandard.assurance.api.helper.BatchRequestCleanup;
import org.goldstandard.assurance.api.helper.RiskSummaryCleanup;
import org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture;
import org.goldstandard.assurance.api.populator.ProjectPopulator;
import org.goldstandard.assurance.api.repository.AuditRepository;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.UserRepository;
import org.goldstandard.assurance.api.service.AuditEventSubject;
import org.goldstandard.assurance.api.util.JsonConverter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/projects";

  private static final Integer ACCOUNT_ID = 1;

  private static final Project INVALID_PROJECT = buildInvalidProject();
  private static final Project TEST_PROJECT = TEST_PROJECTS.get(2);
  private static final Project OWNED_PROJECT = TEST_PROJECTS.get(1);
  private static final Project ASSIGNED_PROJECT = TEST_PROJECTS.getFirst();

  private static final Integer PROJECT_STATUS_ID = 1;
  private static final int DE_CERTIFIED_PROJECT_STATUS_ID = 5;
  private static final Set<Integer> PROJECT_CREDITING_PERIODS = Set.of(1);
  private static final Set<Integer> PROJECT_CP_METHODOLOGIES = Set.of(0);
  private static final Set<Integer> PROJECT_PROJECT_TYPES = Set.of(4);
  private static final Set<Integer> PROJECT_METHODOLOGIES = Set.of(2);
  private static final Set<Integer> PROJECT_SDGS = Set.of(1, 2);

  private static final ProjectClassification PROJECT_CLASSIFICATION = new ProjectClassification(0);

  private static final String UPDATE_SUFFIX = "CHANGED";

  private static final Integer UPDATE_CARBON_STREAM_ID = 1;

  private static final List<ProjectType> ORIGINAL_PROJECT_TYPES =
      List.of(new ProjectType(0), new ProjectType(4));

  private static final List<Integer> UPDATE_PROJECT_TYPE_IDS = List.of(4242);

  private static final List<ProjectLocation> ORIGINAL_PROJECT_LOCATIONS =
      List.of(new ProjectLocation(305));

  private static final List<Integer> UPDATE_PROJECT_LOCATION_IDS = List.of();

  private static final Set<String> CANDIDATE_PARENT_DETAIL_ALLOWED_FIELDS =
      Set.of(
          "id",
          "gsid",
          "name",
          "projectClassificationId",
          "projectClassification",
          "projectStatus",
          "projectStartDate",
          "carbonStream",
          "projectSize",
          "projectTypes",
          "projectLocations",
          "projectAssets",
          "sustainableDevelopmentGoals",
          "methodologies",
          "creditingPeriods");

  private static final Set<String> CANDIDATE_PARENT_DETAIL_FORBIDDEN_FIELDS =
      Set.of(
          "notes",
          "piiMetadata",
          "currentRiskSummary",
          "registryUrl",
          "currentRequestId",
          "feeSchedule",
          "estimatedAnnualUnits",
          "bundle",
          "account",
          "accountId",
          "accountName",
          "accountEmailAddress");

  @Autowired ProjectRepository projectRepository;

  @Autowired ProjectCreditingPeriodRepository projectCreditingPeriodRepository;

  @Autowired AuditRepository auditRepository;

  @Autowired UserRepository userRepository;

  @Autowired ProjectPopulator projectPopulator;

  @Autowired CrossLinkingProjectFixture crossLinkingProjectFixture;

  @Autowired ApiClient client;

  @Autowired BatchRequestCleanup batchCleanup;

  @Autowired RiskSummaryCleanup riskSummaryCleanup;

  @Autowired JsonConverter jsonConverter;

  private static CreditingPeriodDTO createCreditingPeriodDTO(
      Integer periodId, ZonedDateTime testDate) {
    return CreditingPeriodDTO.builder()
        .periodId(periodId)
        .startDate(testDate)
        .endDate(testDate.plusDays(1))
        .methodologies(List.of(createCreditingPeriodMethodologyDTO()))
        .monitoringPeriods(List.of(createMonitoringPeriodDTO(testDate)))
        .build();
  }

  private static MonitoringPeriodDTO createMonitoringPeriodDTO(ZonedDateTime testDate) {
    return MonitoringPeriodDTO.builder()
        .id(0)
        .startDate(testDate)
        .endDate(testDate.plusDays(3))
        .build();
  }

  private static CreditingPeriodMethodologyDTO createCreditingPeriodMethodologyDTO() {
    return CreditingPeriodMethodologyDTO.builder().id(0).version(1.0).build();
  }

  private static ProjectCreateDTO createTestProject(ZonedDateTime testDate, String projectName) {
    var request = new ProjectCreateDTO();
    request.setName(projectName);
    request.setDescription("Project description");
    request.setAccountId(ACCOUNT_ID);
    request.setProjectStatusId(1);
    request.setCarbonStreamId(1);
    request.setFeeScheduleId(0);
    request.setProjectClassificationId(1);
    request.setCurrentStandardId(1);
    request.setRegistrationTypeId(1);
    request.setSizeId(1);
    request.setEstimatedAnnualUnits(200);
    request.setStartDate(testDate);
    request.setCreditingPeriods(List.of(createCreditingPeriodDTO(1, testDate)));
    request.setTypeIds(PROJECT_PROJECT_TYPES.stream().toList());
    request.setSustainableDevelopmentGoalIds(PROJECT_SDGS.stream().toList());
    request.setMethodologyIds(PROJECT_METHODOLOGIES.stream().toList());
    return request;
  }

  private static TransitionProjectDetails createTransitionProjectDetails() {
    var details = new TransitionProjectDetails();
    details.setProjectLinkInOtherScheme("https://a.link");
    details.setTransitionApprovalProcedureId(1);
    details.setTransitionFromOtherSchemeId(1);
    details.setTransitionPathwayId(1);
    return details;
  }

  private static ProjectUpdateDTO createProjectPatch(Project project) {
    var startDate = ZonedDateTime.now().withNano(0);
    return ProjectUpdateDTO.builder()
        .description(project.getDescription() + UPDATE_SUFFIX)
        .name(project.getName() + UPDATE_SUFFIX)
        .accountId(project.getAccountId())
        .carbonStreamId(UPDATE_CARBON_STREAM_ID)
        .typeIds(UPDATE_PROJECT_TYPE_IDS)
        .locationIds(UPDATE_PROJECT_LOCATION_IDS)
        .notes(UPDATE_SUFFIX)
        .masterProjectId(toId(project.getMasterProject()))
        .parentProjectId(toId(project.getParentProject()))
        .startDate(startDate)
        .projectStatusId(PROJECT_STATUS_ID)
        .methodologyIds(PROJECT_METHODOLOGIES.stream().toList())
        .creditingPeriods(
            List.of(
                createCreditingPeriodDTO(1, startDate.plusDays(1)),
                createCreditingPeriodDTO(2, startDate.plusDays(2))))
        .transitionProject(createTransitionProjectDetails())
        .projectClassificationId(project.getProjectClassification().getId())
        .feeScheduleId(1)
        .build();
  }

  private static Integer toId(Project project) {
    return project == null ? null : project.getId();
  }

  private static Project buildInvalidProject() {
    Project project = new Project();
    project.setId(999);
    project.setGsid("GS999");
    return project;
  }

  private static Stream<Arguments> roleAndCandidatePOAClassificationsAndExpectedProjectIds() {
    return Stream.of(
        Arguments.of(PROJECT_DEVELOPER_ROLE, 0, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 1, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 2, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 3, List.of(1)),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 4, List.of(1)),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 5, List.of()),
        Arguments.of(ADMIN_ROLE, 0, List.of()),
        Arguments.of(ADMIN_ROLE, 1, List.of()),
        Arguments.of(ADMIN_ROLE, 2, List.of()),
        Arguments.of(ADMIN_ROLE, 3, List.of(1, 4)),
        Arguments.of(ADMIN_ROLE, 4, List.of(1)),
        Arguments.of(ADMIN_ROLE, 5, List.of()),
        Arguments.of(ARM_TEAM_ROLE, 0, List.of()),
        Arguments.of(ARM_TEAM_ROLE, 1, List.of()),
        Arguments.of(ARM_TEAM_ROLE, 2, List.of()),
        Arguments.of(ARM_TEAM_ROLE, 3, List.of(1, 4)),
        Arguments.of(ARM_TEAM_ROLE, 4, List.of(1)),
        Arguments.of(ARM_TEAM_ROLE, 5, List.of()));
  }

  private static Stream<Arguments>
      roleAndCandidateRealCaseVPAMasterProjectIdsAndExpectedProjectIds() {
    return Stream.of(
        Arguments.of(PROJECT_DEVELOPER_ROLE, 0, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 1, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 2, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 3, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 4, List.of()),
        Arguments.of(PROJECT_DEVELOPER_ROLE, 5, List.of()),
        Arguments.of(ADMIN_ROLE, 0, List.of()),
        Arguments.of(ADMIN_ROLE, 1, List.of()),
        Arguments.of(ADMIN_ROLE, 2, List.of()),
        Arguments.of(ADMIN_ROLE, 3, List.of()),
        Arguments.of(ADMIN_ROLE, 4, List.of()),
        Arguments.of(ADMIN_ROLE, 5, List.of()));
  }

  // TODO: Address Create/Update test interference with query tests
  @Test
  @Order(999)
  void testGetProjectListForAdminReturnsAllProjects() {
    var response = client.getProjects(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var overviews = response.getBody().getContent();
    assertNotNull(overviews);
    assertThat(overviews).hasSize(NUMBER_OF_PROJECTS);

    var lastOverview = overviews.get(NUMBER_OF_PROJECTS - 1);
    assertThat(lastOverview.getId()).isEqualTo(4);
    assertThat(lastOverview.getName()).isEqualTo("Project 4");
    assertThat(lastOverview.getDescription()).isEqualTo("Project Description 4");
    assertThat(lastOverview.getStatus()).isFalse();
    assertThat(lastOverview.getCurrentStandardVersion().getId()).isEqualTo(1);
    assertThat(lastOverview.getCurrentStandardVersion().getCode()).isEqualTo("CDM");
    assertThat(lastOverview.getCurrentStandardVersion().getDescription()).isEqualTo("CDM");
  }

  @Test
  void testGetProjectListForProjectDeveloperReturnsOnlyAssignedProjects() {
    var users = userRepository.findAll();
    var account1User =
        users.stream()
            .filter(u -> u.getAccount() != null && u.getAccount().getId() == 2)
            .findFirst()
            .orElseThrow();

    var response = client.getProjects(PROJECT_DEVELOPER_ROLE, account1User.getIdpUserId());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());

    var overviews = response.getBody().getContent();
    assertNotNull(overviews);
    assertThat(overviews).hasSize(3);

    var lastOverview = overviews.getLast();
    assertThat(lastOverview.getId()).isEqualTo(3);
    assertThat(lastOverview.getName()).isEqualTo("Project 3");
    assertThat(lastOverview.getDescription()).isEqualTo("Project Description 3");
    assertThat(lastOverview.getStatus()).isFalse();
    assertThat(lastOverview.getCurrentStandardVersion().getId()).isEqualTo(1);
    assertThat(lastOverview.getCurrentStandardVersion().getCode()).isEqualTo("CDM");
    assertThat(lastOverview.getCurrentStandardVersion().getDescription()).isEqualTo("CDM");
  }

  @Test
  void testGetProjectListFilteredByBundleId() {
    // The populator ensures that only a single record has a bundleId set
    var filter = String.format("bundleId=%d", TEST_BUNDLE_ID);

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    var overviews = response.getBody().getContent();
    assertNotNull(overviews);

    // The project populator should only allow 1 with this bundle ID
    assertThat(overviews).hasSize(1);
    // Verify the project overview is the one we expect
    var overview = overviews.getFirst();
    assertThat(overview.getId()).isEqualTo(2);
    assertThat(overview.getName()).isEqualTo("Project 2");
    assertThat(overview.getDescription()).isEqualTo("Project Description 2");
    assertThat(overview.getStatus()).isFalse();
    assertThat(overview.getCurrentStandardVersion().getId()).isEqualTo(1);
    assertThat(overview.getCurrentStandardVersion().getCode()).isEqualTo("CDM");
    assertThat(overview.getCurrentStandardVersion().getDescription()).isEqualTo("CDM");
  }

  @Test
  void testGetProjectListFilteredByProjectStatusId() {
    var filter = String.format("projectStatusId=%d", PROJECT_STATUS_ID);

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    assertThat(response.getBody().getContent())
        .isNotNull()
        .map(ProjectDTO::getId)
        .containsExactly(2, 4);
  }

  @Test
  void testGetProjectListFilteredByProjectId() {
    var filter = "projectIds=2,4";

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertNotNull(response.getBody());
    assertThat(response.getBody().getContent())
        .isNotNull()
        .map(ProjectDTO::getId)
        .containsExactly(2, 4);
  }

  @Test
  void testGetProjectListUnauthorizedForProjectDeveloperWithoutAccount() {
    var response = client.getProjects(PROJECT_DEVELOPER_ROLE, NO_ACCOUNT_USER_ID1);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @ParameterizedTest
  @EnumSource(GetProjectTestCase.class)
  void testGetProjectById(GetProjectTestCase testCase) {
    if (!testCase.getProject().equals(INVALID_PROJECT)) {
      projectPopulator.setProjectStatusAsNew(testCase.getProject());
    }

    try {
      var response =
          client.getProject(testCase.getProjectId(), testCase.getRole(), testCase.getUserId());

      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());

      if (response.getStatusCode().equals(HttpStatus.OK)) {
        var detail = response.getBody();
        assertThat(detail).isNotNull();

        assertThat(detail.getId()).isEqualTo(testCase.getProjectId());
        assertThat(detail.getName()).isEqualTo("Project " + testCase.getProjectId());
        assertThat(detail.getAccountId()).isEqualTo(testCase.getAccountId());
        assertThat(detail.getDescription())
            .isEqualTo("Project Description " + testCase.getProjectId());
        assertThat(detail.getStatus()).isFalse();
        var bundle = detail.getBundle();
        assertThat(bundle.getId()).isEqualTo(testCase.getProjectId());
        assertThat(bundle.getName()).isEqualTo("Test Bundle " + testCase.getProjectId());
        assertThat(detail.getCurrentRequestId()).isEqualTo(testCase.getProjectId());
        assertThat(detail.getEstimatedAnnualUnits()).isEqualTo(20 * testCase.getProjectId());
        if (Objects.equals(detail.getId(), ASSIGNED_PROJECT.getId())) {
          assertThat(detail.getMasterProject()).isNull();
        } else {
          assertThat(detail.getMasterProject().getId()).isEqualTo(1);
          assertThat(detail.getMasterProject().getName()).isEqualTo("Project 1");
          assertThat(detail.getMasterProject().getProjectClassificationId())
              .isEqualTo(PROGRAMME_OF_ACTIVITIES_ID);
        }
        if (Objects.equals(testCase.getProjectId(), TEST_PROJECT.getId())) {
          assertThat(detail.getParentProject().getId()).isEqualTo(2);
          assertThat(detail.getParentProject().getName()).isEqualTo("Project 2");
          assertThat(detail.getParentProject().getProjectClassificationId())
              .isEqualTo(REAL_CASE_ID);
        }
        if (detail.getProjectClassification().isRealCaseVpa()
            || detail.getProjectClassification().isRegularCaseVpa()) {
          assertThat(detail.getParentProject()).isNotNull();
        } else {
          assertThat(detail.getParentProject()).isNull();
        }
        assertThat(detail.getDesignCertificationDate().toEpochSecond())
            .isEqualTo(FIXED_DATE.toEpochSecond());
        assertCommonReferenceTypes(detail);
      }
    } finally {
      projectPopulator.resetProjects();
    }
  }

  @ParameterizedTest
  @EnumSource(GetProjectTestCase.class)
  void testGetProjectByGsid(GetProjectTestCase testCase) {
    if (!testCase.getProject().equals(INVALID_PROJECT)) {
      projectPopulator.setProjectStatusAsNew(testCase.getProject());
    }

    try {
      var response =
          client.getProject(testCase.getGsId(), testCase.getRole(), testCase.getUserId());

      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());

      if (response.getStatusCode().equals(HttpStatus.OK)) {
        assertNotNull(response.getBody());

        var detail = response.getBody();
        assertThat(detail.getGsid()).isEqualTo(testCase.getGsId());
        assertThat(detail.getStatus()).isFalse();
        if (Objects.equals(detail.getGsid(), ASSIGNED_PROJECT.getGsid())) {
          assertThat(detail.getMasterProject()).isNull();
        } else {
          assertThat(detail.getMasterProject().getId()).isEqualTo(1);
          assertThat(detail.getMasterProject().getName()).isEqualTo("Project 1");
          assertThat(detail.getMasterProject().getProjectClassificationId())
              .isEqualTo(PROGRAMME_OF_ACTIVITIES_ID);
        }
        assertThat(detail.getDesignCertificationDate().toEpochSecond())
            .isEqualTo(FIXED_DATE.toEpochSecond());
        assertCommonReferenceTypes(detail);
      }
    } finally {
      projectPopulator.resetProjects();
    }
  }

  @Test
  void testCreateTransitionProject() {
    var testDate = ZonedDateTime.now();
    var request = createTestProject(testDate, "Test Transition project");
    request.setTransitionProject(createTransitionProjectDetails());
    var auditedProjectIdsBefore = getAuditedCreatedProjects();

    var response = client.createProject(request, ADMIN_ROLE);

    var projectId = response.getBody();
    try {
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(projectId);
      assertThat(projectId).isPositive();

      var transitionResponse = client.getProject(projectId, ADMIN_ROLE);
      assertThat(transitionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      var transitionProject = transitionResponse.getBody();
      assertThat(transitionProject).isNotNull();
      assertThat(transitionProject.getCreditingPeriods()).hasSize(1);
      assertThat(transitionProject.getAccountId()).isEqualTo(ACCOUNT_ID);
      assertCommonReferenceTypes(transitionProject);

      // Clean up
      projectCreditingPeriodRepository.deleteById(new ProjectCreditingPeriodId(projectId, 1));
      projectRepository.deleteById(projectId);

      // Verify audit event was created
      var auditedProjectIdsAfter = getAuditedCreatedProjects();
      assertThat(auditedProjectIdsAfter.size() - auditedProjectIdsBefore.size()).isEqualTo(1);
      // The audit event should store the projectId of the project that was created
      assertThat(auditedProjectIdsBefore).doesNotContain(projectId);
      assertThat(auditedProjectIdsAfter).contains(projectId);
    } finally {
      projectPopulator.deleteProject(projectId);
    }
  }

  @Test
  void testCreateProjectUnauthenticatedIsDenied() {
    var date = ZonedDateTime.now();
    var request = createTestProject(date, "Test project create unauthenticated");

    var response = client.createProjectUnauthenticated(request);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testCreateProjectVVBIsDenied() {
    var request = createTestProject(ZonedDateTime.now(), "Test project");

    var response = client.createProject(request, VVB_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testPDCreateProjectForOtherAccountDenied() {
    var request = createTestProject(ZonedDateTime.now(), "Test project");
    request.setAccountId(2);

    var response = client.createProject(request, PROJECT_DEVELOPER_ROLE, NO_ACCOUNT_USER_ID1);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  void testCreateProjectFailsForDuplicateCreditingPeriods() {
    var testDate = ZonedDateTime.now();
    var request = createTestProject(testDate, "Test project with duplicate crediting periods");
    // duplicate crediting period
    request.setCreditingPeriods(
        List.of(
            request.getCreditingPeriods().getFirst(), request.getCreditingPeriods().getFirst()));
    var auditedProjectIdsBefore = getAuditedCreatedProjects();

    var response = client.createProject(request, ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // Verify no audit event created
    var auditedProjectIdsAfter = getAuditedCreatedProjects();
    assertThat(auditedProjectIdsAfter).hasSize(auditedProjectIdsBefore.size());
  }

  @Test
  void testCreateProjectFailsForInvalidCreditingPeriodId() {
    var testDate = ZonedDateTime.now();
    var request = createTestProject(testDate, "Test project with invalid crediting period id");
    request.setCreditingPeriods(List.of(createCreditingPeriodDTO(0, testDate)));
    var auditedProjectIdsBefore = getAuditedCreatedProjects();

    var response = client.createProject(request, ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // Verify no audit event created
    var auditedProjectIdsAfter = getAuditedCreatedProjects();
    assertThat(auditedProjectIdsAfter).hasSize(auditedProjectIdsBefore.size());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "search=ect 2",
        "name=ect 2",
        "search=gs1002",
        "search=GS1002",
        "search=1002",
        "gsid=GS1002"
      })
  void testGetProjectListFilteredByNameSingleResult(String filter) {
    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    var body = response.getBody();
    assertThat(body).isNotNull();
    var overviews = body.getContent();
    // The project populator should only allow 1 with this GSID
    assertThat(overviews).hasSize(1);
    // Verify the project overview is the one we expect
    var overview = overviews.getFirst();
    assertThat(overview.getId()).isEqualTo(2);
    assertThat(overview.getName()).isEqualTo("Project 2");
    assertThat(overview.getDescription()).isEqualTo("Project Description 2");
  }

  @ParameterizedTest
  @ValueSource(strings = {"search=Project", "name=Project"})
  void testGetProjectListFilteredByNameMultiResult(String filter) {
    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    var overviews = body.getContent();
    // All project created are returned
    assertThat(overviews).hasSize(NUMBER_OF_PROJECTS);
  }

  @Test
  void testGetProjectListFilteredByAccount() {
    var filter = "accountId=3";

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    var overviews = body.getContent();
    // Just one project with account id 3
    assertThat(overviews).map(ProjectDTO::getName).containsExactly("Project 3");
  }

  @Test
  void testGetProjectListFilteredByMasterProject() {
    var filter = "masterProjectId=1";

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    var overviews = body.getContent();
    // 1st test project is the parent of the remaining 2
    assertThat(overviews).map(ProjectDTO::getId).containsExactly(2, 3);
  }

  @Test
  void testGetProjectListFilteredByParentProject() {
    var filter = "parentProjectId=2";

    var response = client.getFilteredProjects(ADMIN_ROLE, filter);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var body = response.getBody();
    assertThat(body).isNotNull();
    var overviews = body.getContent();
    // 2nd test project is the parent of the 3rd
    assertThat(overviews).map(ProjectDTO::getId).containsExactly(3);
  }

  @Test
  void testUpdateProject() {
    // TODO: More exhaustive field testing
    var project = createAndSaveTestProject();
    try {
      var update = createProjectPatch(project);

      var response = client.updateProject(project.getId(), update);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var updatedProject = projectRepository.findById(project.getId()).orElseThrow();
      assertThat(updatedProject.getName()).isEqualTo(update.getName());
      assertThat(updatedProject.getAccountId()).isEqualTo(update.getAccountId());
      assertThat(updatedProject.getDescription()).isEqualTo(update.getDescription());
      assertThat(updatedProject.getCarbonStream().getId()).isEqualTo(update.getCarbonStreamId());
      assertThat(updatedProject.getProjectTypes().stream().map(ProjectType::getId))
          .containsExactlyElementsOf(update.getTypeIds());
      assertThat(updatedProject.getProjectLocations().stream().map(ProjectLocation::getId))
          .containsExactlyElementsOf(update.getLocationIds());
      assertThat(updatedProject.getNotes()).isEqualTo(update.getNotes());
      assertThat(updatedProject.getProjectStartDate()).isEqualTo(update.getStartDate());
      assertThat(toId(updatedProject.getMasterProject())).isEqualTo(update.getMasterProjectId());
      assertThat(toId(updatedProject.getParentProject())).isEqualTo(update.getParentProjectId());
      assertThat(
              updatedProject.getCreditingPeriods().stream()
                  .map(ProjectCreditingPeriod::getPeriodId)
                  .distinct())
          .containsExactlyElementsOf(
              update.getCreditingPeriods().stream().map(CreditingPeriodDTO::getPeriodId).toList());
      assertThat(
              updatedProject.getCreditingPeriods().stream()
                  .map(ProjectCreditingPeriod::getMonitoringPeriods)
                  .flatMap(mp -> mp.stream().map(ProjectCreditingMonitoringPeriod::getId)))
          .containsExactlyInAnyOrderElementsOf(
              update.getCreditingPeriods().stream()
                  .map(CreditingPeriodDTO::getMonitoringPeriods)
                  .flatMap(mp -> mp.stream().map(MonitoringPeriodDTO::getId))
                  .toList());
      assertThat(
              updatedProject.getCreditingPeriods().stream()
                  .map(ProjectCreditingPeriod::getMethodologies)
                  .flatMap(
                      mp -> mp.stream().map(ProjectCreditingPeriodMethodology::getMethodologyId)))
          .containsExactlyInAnyOrderElementsOf(
              update.getCreditingPeriods().stream()
                  .map(CreditingPeriodDTO::getMethodologies)
                  .flatMap(mp -> mp.stream().map(CreditingPeriodMethodologyDTO::getId))
                  .toList());
      assertThat(updatedProject.getRegistrationType()).isNull();
      assertThat(updatedProject.getTransitionProject()).isNotNull();
      assertThat(updatedProject.getTransitionProject().getProjectLinkInOtherScheme())
          .isEqualTo(update.getTransitionProject().getProjectLinkInOtherScheme());
      assertThat(updatedProject.getTransitionProject().getTransitionPathway().getId())
          .isEqualTo(update.getTransitionProject().getTransitionPathwayId());
      assertThat(updatedProject.getProjectClassification().getId())
          .isEqualTo(update.getProjectClassificationId());
      validateUpdateAuditEvents(project.getId(), update);
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @Test
  void testUpdateProjectStoresRegistryId() {
    var project = createAndSaveTestProject();
    try {
      var update = createProjectPatch(project);

      var updateResponse = client.updateProject(project.getId(), update);

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      Awaitility.await()
          .atMost(Duration.ofSeconds(5))
          .until(() -> projectHasRegistryId(project.getId()));

      var getResponse = client.getProject(project.getId(), ADMIN_ROLE);

      assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      var updatedProject = getResponse.getBody();
      assertThat(updatedProject).isNotNull();
      var expectedId = project.getId() + 999;

      assertThat(updatedProject.getRegistryUrl())
          .contains(String.format("http://localhost:3002/projects/details/%d", expectedId));
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @Test
  void testUpdateProjectUnauthorizedIfUnauthenticated() {
    var project = createAndSaveTestProject();
    var patch = createProjectPatch(project);
    try {
      var response = client.updateProjectUnauthenticated(project.getId(), patch);

      assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @ParameterizedTest
  @EnumSource(ProjectUpdateCase.class)
  void testUpdateProjectPermission(ProjectUpdateCase testCase) {
    var project = createAndSaveTestProject();
    var patch = createProjectPatch(project);

    try {
      var response =
          client.updateProject(project.getId(), patch, testCase.getRole(), testCase.getUserId());

      assertThat(response.getStatusCode()).isEqualTo(testCase.getExpectedStatus());
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @Test
  void testProjectDeveloperCannotTamperProjectUpdateFields() {
    var project = createAndSaveTestProject();
    var patch = createProjectPatch(project);

    try {
      var response =
          client.updateProject(
              project.getId(), patch, PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT1_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var persistedProject = projectRepository.findById(project.getId()).orElseThrow();
      assertThat(persistedProject.getName()).isEqualTo(patch.getName());
      assertThat(persistedProject.getAccountId()).isEqualTo(project.getAccountId());
      assertThat(persistedProject.getDescription()).isEqualTo(project.getDescription());
      assertThat(persistedProject.getCarbonStream()).isNull();
      assertThat(persistedProject.getNotes()).isEqualTo(project.getNotes());
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {ProjectStatus.LISTED, DE_CERTIFIED_PROJECT_STATUS_ID})
  void testAdminCanUpdateListedAndDecertifiedProjects(int projectStatusId) {
    var project = createAndSaveTestProject();
    try {
      project.setProjectStatus(new ProjectStatus(projectStatusId));
      projectRepository.save(project);
      var patch = createProjectPatch(project);

      var response = client.updateProject(project.getId(), patch, ADMIN_ROLE, NO_ACCOUNT_USER_ID1);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var persistedProject = projectRepository.findById(project.getId()).orElseThrow();
      assertThat(persistedProject.getName()).isEqualTo(patch.getName());
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {ProjectStatus.LISTED, DE_CERTIFIED_PROJECT_STATUS_ID})
  void testProjectDeveloperCannotUpdateListedOrLaterProject(int projectStatusId) {
    var project = createAndSaveTestProject();
    try {
      project.setProjectStatus(new ProjectStatus(projectStatusId));
      projectRepository.save(project);
      var patch = createProjectPatch(project);

      var response =
          client.updateProject(
              project.getId(), patch, PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT1_USER_ID);

      assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  @Test
  void shouldReturnProjectTrees() {
    var response = client.getProjectTrees(ADMIN_ROLE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var pages = response.getBody();
    assertThat(pages.getTotalElements()).isEqualTo(2);
    var root = pages.getContent().getFirst();
    assertThat(root.getId()).isEqualTo(1);
    assertThat(root.getChildren()).map(ProjectTreeNodeDTO::getId).containsExactly(2);
    assertThat(root.getChildren())
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .map(ProjectTreeNodeDTO::getId)
        .containsExactly(3);
    assertThat(root.getChildren())
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .isEmpty();
  }

  @Test
  void shouldReturnProjectTree() {
    var response = client.getProjectTree(ADMIN_ROLE, 3);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var root = response.getBody();
    assertThat(root.getId()).isEqualTo(1);
    assertThat(root.getChildren()).map(ProjectTreeNodeDTO::getId).containsExactly(2);
    assertThat(root.getChildren())
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .map(ProjectTreeNodeDTO::getId)
        .containsExactly(3);
    assertThat(root.getChildren())
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .isEmpty();
  }

  @Test
  void treeNodesShouldHaveBundle() {
    var response = client.getProjectTree(ADMIN_ROLE, 3);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    var root = response.getBody();
    assertThat(root.getChildren())
        .map(ProjectTreeNodeDTO::getBundle)
        .map(BundleDTO::getId)
        .containsExactly(2);
    assertThat(root.getChildren())
        .flatMap(ProjectTreeNodeDTO::getChildren)
        .map(ProjectTreeNodeDTO::getBundle)
        .map(BundleDTO::getId)
        .containsExactly(3);
  }

  @Test
  void shouldUpdateProjectStatus() {
    var project = TEST_PROJECTS.getFirst();
    try {
      var projectStatus = project.getProjectStatus();
      var updateDto =
          ProjectStatusUpdateDTO.builder()
              .projectStatusId(projectStatus.getId() + 1)
              .requestId(1)
              .build();

      var response = client.updateProjectStatus(ARM_TEAM_ROLE, project.getId(), updateDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).containsExactly(project.getId());
    } finally {
      projectPopulator.resetProjects();
    }
  }

  @Test
  void shouldUpdateAllProjectsInBatchRequest() {
    var projectPoa = TEST_PROJECTS.getFirst();
    var projectRealCase = TEST_PROJECTS.get(1);
    var createDto = CreateBatchRequestDTOMother.createBatchDTO(projectPoa, projectRealCase);
    var createBatchResponse = client.createBatchRequest(createDto);
    var batchId = createBatchResponse.getBody();

    try {
      var getBatchResponse = client.getPublicBatchProjects(batchId);
      var requestId = Objects.requireNonNull(getBatchResponse.getBody()).getParent().getRequestId();

      var updateDto =
          ProjectStatusUpdateDTO.builder().projectStatusId(3).requestId(requestId).build();
      var response = client.updateProjectStatus(ARM_TEAM_ROLE, projectPoa.getId(), updateDto);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).containsExactly(projectPoa.getId(), projectRealCase.getId());
    } finally {
      projectPopulator.resetProjects();
      batchCleanup.deleteBatchRequests(batchId);
    }
  }

  @ParameterizedTest
  @MethodSource("roleAndCandidatePOAClassificationsAndExpectedProjectIds")
  void shouldGetCandidatePOAs(
      String userRole, int classificationId, Collection<Integer> expectedProjectIds) {
    var request =
        GetCandidatePOAsRequest.builder().accountId(1).classificationId(classificationId).build();

    var response =
        PROJECT_DEVELOPER_ROLE.equals(userRole)
            ? client.getCandidatePOAs(userRole, ASSIGNED_ACCOUNT1_USER_ID, request)
            : client.getCandidatePOAs(userRole, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .map(Project::getId)
        .containsExactlyElementsOf(expectedProjectIds);
  }

  @ParameterizedTest
  @MethodSource("roleAndCandidateRealCaseVPAMasterProjectIdsAndExpectedProjectIds")
  void shouldGetCandidateRealCaseVPAs(
      String userRole, int masterProjectId, Collection<Integer> expectedProjectIds) {
    var request =
        GetCandidateRealCaseVPAsRequest.builder()
            .accountId(1)
            .masterProjectId(masterProjectId)
            .classificationId(ProjectClassification.REGULAR_CASE_VPA_ID)
            .build();

    var response =
        PROJECT_DEVELOPER_ROLE.equals(userRole)
            ? client.getCandidateRealCaseVPAs(userRole, ASSIGNED_ACCOUNT1_USER_ID, request)
            : client.getCandidateRealCaseVPAs(userRole, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .map(Project::getId)
        .containsExactlyElementsOf(expectedProjectIds);
  }

  @Test
  void shouldApplyCrossLinkCandidatePoliciesThroughApi() {
    var projects = crossLinkingProjectFixture.populate();
    try {
      assertCrossLinkCandidatePOAsIncludeBetaPOAs(projects);
      assertCrossLinkCandidateRealCaseVPAsIncludeBetaRealCase(projects);
      assertCrossLinkCandidateParentDetailAllowsBetaNewPoa(projects);
      assertInvalidCrossLinkCandidateParentDetailIsRejected(projects);
      assertProjectDeveloperCannotSpoofCandidateAccount(projects);
      assertProjectDeveloperCannotSpoofCandidateParentDetailAccount(projects);
      assertNativeCreateRejectsCrossAccountParents(projects);
      assertBetaProjectDeveloperTreeDoesNotExposeAlphaCrossLinkChild(projects);
    } finally {
      cleanupCrossLinkingFixture(projects);
    }
  }

  private void assertCrossLinkCandidatePOAsIncludeBetaPOAs(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    for (int classificationId :
        List.of(
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID)) {
      var request =
          GetCandidatePOAsRequest.builder()
              .accountId(projects.alphaAccount().getId())
              .classificationId(classificationId)
              .build();

      var response = client.getCandidatePOAs(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody())
          .map(Project::getId)
          .contains(
              projects.betaNewPoaWithoutRealCase().getId(),
              projects.betaListedPoa().getId(),
              projects.betaDesignCertifiedPoa().getId());
    }
  }

  private void assertCrossLinkCandidateRealCaseVPAsIncludeBetaRealCase(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request =
        GetCandidateRealCaseVPAsRequest.builder()
            .accountId(projects.alphaAccount().getId())
            .masterProjectId(projects.betaListedPoa().getId())
            .classificationId(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID)
            .build();

    var response =
        client.getCandidateRealCaseVPAs(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .map(Project::getId)
        .contains(projects.betaRealCaseAcm0001().getId());
  }

  private void assertCrossLinkCandidateParentDetailAllowsBetaNewPoa(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request =
        GetCandidateParentProjectDetailsRequest.builder()
            .accountId(projects.alphaAccount().getId())
            .classificationId(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID)
            .candidateProjectId(projects.betaNewPoaWithoutRealCase().getId())
            .build();

    var detailResponse =
        client.getCandidateParentDetails(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);
    var rawDetailResponse =
        client.getCandidateParentDetailsJson(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);
    var normalGetResponse =
        client.getProject(
            projects.betaNewPoaWithoutRealCase().getId(), PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID);

    assertThat(detailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(detailResponse.getBody()).isNotNull();
    assertThat(detailResponse.getBody().getId())
        .isEqualTo(projects.betaNewPoaWithoutRealCase().getId());
    assertThat(detailResponse.getBody().getProjectAssets()).isNotEmpty();
    assertThat(detailResponse.getBody().getMethodologies())
        .singleElement()
        .satisfies(
            methodology -> {
              assertThat(methodology.getId()).isEqualTo(0);
              assertThat(methodology.getCode()).isEqualTo("ACM0001_FLARING_OR_USE_OF_LANDFILL_GAS");
              assertThat(methodology.getDescription())
                  .isEqualTo("ACM0001 Flaring or use of landfill gas");
            });
    assertThat(detailResponse.getBody().getCreditingPeriods()).isEmpty();
    assertThat(rawDetailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertCandidateParentDetailJsonContract(rawDetailResponse.getBody());
    assertCandidateParentMethodologyJsonContract(rawDetailResponse.getBody());
    assertThat(normalGetResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  private void assertInvalidCrossLinkCandidateParentDetailIsRejected(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request =
        GetCandidateParentProjectDetailsRequest.builder()
            .accountId(projects.alphaAccount().getId())
            .classificationId(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID)
            .candidateProjectId(projects.betaRealCaseAcm0001().getId())
            .masterProjectId(projects.betaDesignCertifiedPoa().getId())
            .build();

    var response =
        client.getCandidateParentDetails(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  private void assertProjectDeveloperCannotSpoofCandidateAccount(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request =
        GetCandidatePOAsRequest.builder()
            .accountId(projects.betaAccount().getId())
            .classificationId(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID)
            .build();

    var response = client.getCandidatePOAsJson(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  private void assertProjectDeveloperCannotSpoofCandidateParentDetailAccount(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request =
        GetCandidateParentProjectDetailsRequest.builder()
            .accountId(projects.betaAccount().getId())
            .classificationId(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID)
            .candidateProjectId(projects.betaNewPoaWithoutRealCase().getId())
            .build();

    var response =
        client.getCandidateParentDetails(PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  private void assertNativeCreateRejectsCrossAccountParents(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request = createTestProject(FIXED_DATE.plusDays(1), "Rejected native cross-account VPA");
    request.setAccountId(projects.alphaAccount().getId());
    request.setProjectClassificationId(ProjectClassification.REAL_CASE_VPA_ID);
    request.setMasterProjectId(projects.betaListedPoa().getId());
    request.setParentProjectId(projects.betaListedPoa().getId());

    var response = client.createProject(request, PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  private void assertBetaProjectDeveloperTreeDoesNotExposeAlphaCrossLinkChild(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var crossLinkChild = createAlphaCrossLinkChild(projects);
    try {
      var singleTreeResponse =
          client.getProjectTree(
              PROJECT_DEVELOPER_ROLE, BETA_PD_USER_ID, projects.betaListedPoa().getId());
      var pageTreeResponse = client.getProjectTrees(PROJECT_DEVELOPER_ROLE, BETA_PD_USER_ID);

      assertThat(singleTreeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(flattenTree(singleTreeResponse.getBody()))
          .map(ProjectTreeNodeDTO::getId)
          .doesNotContain(crossLinkChild.getId())
          .contains(projects.betaListedPoa().getId(), projects.betaRealCaseAcm0001().getId());

      assertThat(pageTreeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(pageTreeResponse.getBody()).isNotNull();
      assertThat(
              pageTreeResponse.getBody().getContent().stream()
                  .flatMap(root -> flattenTree(root).stream())
                  .map(ProjectTreeNodeDTO::getId)
                  .toList())
          .doesNotContain(crossLinkChild.getId())
          .contains(projects.betaListedPoa().getId(), projects.betaRealCaseAcm0001().getId());

      var directGetResponse =
          client.getProject(crossLinkChild.getId(), PROJECT_DEVELOPER_ROLE, BETA_PD_USER_ID);

      assertThat(directGetResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    } finally {
      projectPopulator.deleteProject(crossLinkChild.getId());
    }
  }

  private Project createAlphaCrossLinkChild(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var request = createTestProject(FIXED_DATE.plusDays(2), "Alpha cross-link tree child");
    request.setAccountId(projects.alphaAccount().getId());
    request.setProjectClassificationId(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);
    request.setMasterProjectId(projects.betaListedPoa().getId());
    request.setParentProjectId(projects.betaListedPoa().getId());

    var response = client.createProject(request, PROJECT_DEVELOPER_ROLE, ALPHA_PD_USER_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    return projectRepository.findById(response.getBody()).orElseThrow();
  }

  private static List<ProjectTreeNodeDTO> flattenTree(ProjectTreeNodeDTO root) {
    if (root == null) {
      return List.of();
    }
    return Stream.concat(
            Stream.of(root),
            root.getChildren().stream().flatMap(child -> flattenTree(child).stream()))
        .toList();
  }

  private void assertCandidateParentDetailJsonContract(String responseBody) {
    var fields = jsonConverter.toMap(responseBody).keySet();

    assertThat(fields).containsExactlyInAnyOrderElementsOf(CANDIDATE_PARENT_DETAIL_ALLOWED_FIELDS);
    assertThat(fields).doesNotContainAnyElementsOf(CANDIDATE_PARENT_DETAIL_FORBIDDEN_FIELDS);
  }

  @SuppressWarnings("unchecked")
  private void assertCandidateParentMethodologyJsonContract(String responseBody) {
    var fields = jsonConverter.toMap(responseBody);
    var methodologies = (List<Map<String, Object>>) fields.get("methodologies");

    assertThat(methodologies).hasSize(1);
    assertThat(methodologies.getFirst()).containsOnlyKeys("id", "code", "description");
  }

  private void cleanupCrossLinkingFixture(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    projectPopulator.deleteProjects(
        projects.betaRealCaseAcm0001(),
        projects.betaRealCaseAcm0002(),
        projects.betaNewPoaWithoutRealCase(),
        projects.betaListedPoa(),
        projects.betaDesignCertifiedPoa());
  }

  @Test
  void shouldRetrieveMethodologyHistoryAfterUpdate() {
    var project = createAndSaveTestProject();
    try {
      var update = updateCreditingPeriod(project);
      client.updateProject(project.getId(), update);

      var historyResponse = client.getProjectMethodologyHistory(project.getId(), ADMIN_ROLE);
      assertThat(historyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      var history = Objects.requireNonNull(historyResponse.getBody()).getContent();

      var actualMethodologyUpdates =
          history.stream().flatMap(h -> h.getUpdates().stream()).toList();
      var updatedMethodologies =
          update.getCreditingPeriods().stream()
              .flatMap(c -> c.getMethodologies().stream())
              .toList();
      var verdicts = history.stream().map(MethodologyHistoryDTO::getVerdict).toList();
      assertThat(history).hasSize(update.getCreditingPeriods().size());
      assertThat(verdicts).containsOnly(ProjectUpdateVerdict.PROJECT_FORCED.name());
      assertThat(actualMethodologyUpdates).hasSize(updatedMethodologies.size());
      assertThat(actualMethodologyUpdates.stream().map(MethodologyUpdateDTO::getOperation).toList())
          .allMatch(m -> m.equals(MethodologyOperation.UPDATE.name()));
    } finally {
      projectPopulator.deleteProject(project.getId());
    }
  }

  private static void assertCommonReferenceTypes(ProjectDTO project) {
    assertThat(toIds(project.getProjectTypes(), ProjectType::getId))
        .isEqualTo(PROJECT_PROJECT_TYPES);
    assertThat(toIds(project.getMethodologies(), Methodology::getId))
        .isEqualTo(PROJECT_METHODOLOGIES);
    assertThat(toIds(project.getSustainableDevelopmentGoals(), SustainableDevelopmentGoal::getId))
        .isEqualTo(PROJECT_SDGS);
    assertThat(toIds(project.getCreditingPeriods(), CreditingPeriodDTO::getPeriodId))
        .isEqualTo(PROJECT_CREDITING_PERIODS);
    assertThat(
            toIds(
                project.getCreditingPeriods().stream()
                    .flatMap(cp -> cp.getMethodologies().stream())
                    .toList(),
                CreditingPeriodMethodologyDTO::getId))
        .isEqualTo(PROJECT_CP_METHODOLOGIES);
  }

  private static ProjectUpdateDTO updateCreditingPeriod(Project project) {
    var firstPeriod = IterableUtils.get(project.getCreditingPeriods(), 0);
    var startDate = ZonedDateTime.now().withNano(0);
    var firstPeriodDto = createCreditingPeriodDTO(firstPeriod.getPeriodId(), startDate.plusDays(1));
    return ProjectUpdateDTO.builder()
        .name("test")
        .accountId(project.getAccountId())
        .creditingPeriods(List.of(firstPeriodDto))
        .projectClassificationId(project.getProjectClassification().getId())
        .masterProjectId(toId(project.getMasterProject()))
        .parentProjectId(toId(project.getParentProject()))
        .typeIds(ORIGINAL_PROJECT_TYPES.stream().map(ProjectType::getId).toList())
        .methodologyIds(PROJECT_METHODOLOGIES.stream().toList())
        .projectStatusId(PROJECT_STATUS_ID)
        .feeScheduleId(0)
        .description("test description")
        .build();
  }

  private Project createAndSaveTestProject() {
    var project = new Project();
    project.setName("Test Project");
    project.setProjectClassification(PROJECT_CLASSIFICATION);
    project.setAccountId(ACCOUNT_ID);
    project.setRegistrationType(new RegistrationType(1));
    project.setProjectTypes(ORIGINAL_PROJECT_TYPES);
    project.setProjectLocations(ORIGINAL_PROJECT_LOCATIONS);
    project.setProjectStatus(ProjectStatus.newStatus());
    project.setFeeSchedule(new FeeSchedule(1));
    var saved = projectRepository.save(project);
    var periods = List.of(projectPopulator.buildCreditingPeriod(saved, ZonedDateTime.now()));
    saved.setCreditingPeriods(periods);
    projectCreditingPeriodRepository.saveAll(periods);
    return saved;
  }

  private void validateUpdateAuditEvents(Integer projectId, ProjectUpdateDTO update) {
    var events =
        auditRepository.findAllByEventSubject(AuditEventSubject.PROJECT_UPDATE.name()).stream()
            .filter(
                event -> {
                  var map = jsonConverter.toMap(event.getAdditionalInformation());
                  return Objects.equals(map.get("id"), projectId);
                })
            .toList();
    assertThat(events).hasSize(1);
    var additionalInformation = jsonConverter.toMap(events.getFirst().getAdditionalInformation());
    assertThat(additionalInformation)
        .contains(
            entry("description", update.getDescription()),
            entry("name", update.getName()),
            entry("accountId", update.getAccountId()),
            entry("masterProject", update.getMasterProjectId()),
            entry("parentProject", update.getParentProjectId()),
            entry("notes", update.getNotes()));
    assertThat(additionalInformation)
        .extractingByKey("carbonStream")
        .extracting("id")
        .isEqualTo(update.getCarbonStreamId());
    assertThat(additionalInformation)
        .extractingByKey("projectClassification")
        .extracting("id")
        .isEqualTo(update.getProjectClassificationId());
    List<?> types = (List<?>) additionalInformation.get("projectTypes");
    List<Integer> typeIds = getIds(types);
    assertThat(update.getTypeIds()).containsAll(typeIds);

    List<?> locations = (List<?>) additionalInformation.get("projectLocations");
    List<Integer> locationIds = getIds(locations);
    assertThat(update.getLocationIds()).containsAll(locationIds);
  }

  private static List<Integer> getIds(List<?> refDataList) {
    return refDataList.stream()
        .filter(Map.class::isInstance)
        .map(type -> (Integer) ((Map<?, ?>) type).get("id"))
        .toList();
  }

  @Getter
  @AllArgsConstructor
  public enum GetProjectTestCase {
    ADMIN_PROJECT_NOT_FOUND(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, INVALID_PROJECT, HttpStatus.NOT_FOUND),
    NON_ADMIN_PROJECT_NOT_FOUND(
        VVB_ROLE, ASSIGNED_ACCOUNT1_USER_ID, INVALID_PROJECT, HttpStatus.NOT_FOUND),
    ADMIN_CAN_SEE_PROJECT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, TEST_PROJECT, HttpStatus.OK),
    ARM_CAN_SEE_PROJECT(ARM_TEAM_ROLE, ARM_USER_ID, TEST_PROJECT, HttpStatus.OK),
    FINANCE_CAN_SEE_PROJECT(FINANCE_ROLE, ASSIGNED_ACCOUNT1_USER_ID, TEST_PROJECT, HttpStatus.OK),
    TAC_CAN_SEE_PROJECT(TAC_ROLE, ASSIGNED_ACCOUNT1_USER_ID, TEST_PROJECT, HttpStatus.OK),
    PD_CANNOT_SEE_NON_OWNED_PROJECT(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT2_USER_ID, TEST_PROJECT, UNAUTHORIZED),
    PD_CAN_SEE_OWNED_PROJECT(
        PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT2_USER_ID, OWNED_PROJECT, HttpStatus.OK),
    VVB_CANNOT_SEE_UNASSIGNED_PROJECT(
        VVB_ROLE, ASSIGNED_ACCOUNT2_USER_ID, TEST_PROJECT, UNAUTHORIZED),
    VVB_CAN_SEE_ASSIGNED_PROJECT(
        VVB_ROLE, ASSIGNED_ACCOUNT2_USER_ID, ASSIGNED_PROJECT, HttpStatus.OK),
    REVIEWER_CANNOT_SEE_UNASSIGNED_PROJECT(
        GS_REVIEWER_ROLE, ASSIGNED_ACCOUNT2_USER_ID, TEST_PROJECT, UNAUTHORIZED),
    REVIEWER_CAN_SEE_ASSIGNED_PROJECT(
        GS_REVIEWER_ROLE, ASSIGNED_ACCOUNT2_USER_ID, ASSIGNED_PROJECT, HttpStatus.OK);
    private final String role;
    private final UUID userId;
    private final Project project;
    private final HttpStatus expectedStatus;

    public Integer getProjectId() {
      return project.getId();
    }

    public String getGsId() {
      return project.getGsid();
    }

    public Integer getAccountId() {
      return project.getAccountId();
    }
  }

  @Getter
  @AllArgsConstructor
  public enum ProjectUpdateCase {
    ADMIN_OMNIPOTENT(ADMIN_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.OK),
    ARM_TEAM(ARM_TEAM_ROLE, NO_ACCOUNT_USER_ID1, HttpStatus.OK),
    PROJECT_DEVELOPER_OWN_PROJECT(PROJECT_DEVELOPER_ROLE, ASSIGNED_ACCOUNT1_USER_ID, HttpStatus.OK),
    PROJECT_DEVELOPER_OTHER_PROJECT(PROJECT_DEVELOPER_ROLE, NO_ACCOUNT_USER_ID1, UNAUTHORIZED);
    private final String role;
    private final UUID userId;
    private final HttpStatus expectedStatus;
  }

  private static <R> Set<Integer> toIds(List<R> data, Function<R, Integer> function) {
    return data.stream().map(function).collect(Collectors.toSet());
  }

  @Test
  void shouldCreateProjectRiskSummary() {
    var project = createAndSaveTestProject();
    var now = LocalDateTime.now().withNano(0);

    try {
      var createDto =
          ProjectRiskSummaryCreateDTO.builder()
              .risk(true)
              .summary("Test risk summary")
              .dateApplied(now)
              .build();
      var response =
          client.createProjectRiskSummary(
              project.getId(), createDto, ADMIN_ROLE, NO_ACCOUNT_USER_ID1);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

      var body = response.getBody();
      assertThat(body).isNotNull();
      assertThat(body.getProjectId()).isEqualTo(project.getId());
      assertThat(body.getRisk()).isTrue();
      assertThat(body.getSummary()).isEqualTo("Test risk summary");
      assertThat(body.getDateApplied()).isEqualTo(now);
      assertThat(body.getUser()).isNull();
    } finally {
      riskSummaryCleanup.deleteAllByProjectId(project.getId());
      projectPopulator.deleteProject(project.getId());
    }
  }

  @Test
  void shouldListProjectRiskSummaries() {
    var project = createAndSaveTestProject();
    var now = LocalDateTime.now().withNano(0);

    try {
      var createDto =
          ProjectRiskSummaryCreateDTO.builder()
              .risk(true)
              .summary("Test risk summary for list")
              .dateApplied(now)
              .build();
      client.createProjectRiskSummary(project.getId(), createDto, ADMIN_ROLE, NO_ACCOUNT_USER_ID1);

      var response = client.getProjectRiskSummaries(project.getId(), ADMIN_ROLE);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      var body = response.getBody();
      assertThat(body).isNotNull();
      var summaries = body.getContent();
      assertThat(summaries)
          .anySatisfy(
              summary -> {
                assertThat(summary.getSummary()).isEqualTo("Test risk summary for list");
                assertThat(summary.getRisk()).isTrue();
                assertThat(summary.getDateApplied()).isEqualTo(now);
                assertThat(summary.getUser()).isEqualTo("Test 1");
              });
    } finally {
      riskSummaryCleanup.deleteAllByProjectId(project.getId());
      projectPopulator.deleteProject(project.getId());
    }
  }

  private Set<Integer> getAuditedCreatedProjects() {
    return auditRepository.findAllByEventSubject(AuditEventSubject.PROJECT_CREATE.name()).stream()
        .map(AuditEvent::getAdditionalInformation)
        .map(jsonConverter::toMap)
        .map(map -> (Integer) map.get("id"))
        .collect(Collectors.toSet());
  }

  private boolean projectHasRegistryId(Integer projectId) {
    var updatedProject = projectRepository.findById(projectId);
    if (updatedProject.isEmpty()) {
      return false;
    }
    return updatedProject.map(Project::getRegistryId).map(Optional::isPresent).orElse(false);
  }
}
