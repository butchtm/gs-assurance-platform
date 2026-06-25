package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ACM0001_CREDITING_PERIOD_END;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ACM0001_CREDITING_PERIOD_START;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ACM0002_CREDITING_PERIOD_END;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ACM0002_CREDITING_PERIOD_START;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ALPHA_ACCOUNT_NAME;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.ALPHA_PD_USER_ID;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.BETA_ACCOUNT_NAME;
import static org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture.BETA_PD_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.populator.CrossLinkingProjectFixture;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class CrossLinkingProjectFixtureIntegrationTest extends AbstractApiTest {

  @Autowired CrossLinkingProjectFixture fixture;

  @Autowired ProjectRepository projectRepository;

  @Autowired ProjectCreditingPeriodRepository creditingPeriodRepository;

  @Test
  @Transactional
  void populateCreatesAndResetsCrossLinkingDemoProjects() {
    var projects = fixture.populate();

    assertAccount(projects.alphaAccount(), ALPHA_ACCOUNT_NAME);
    assertAccount(projects.betaAccount(), BETA_ACCOUNT_NAME);
    assertUser(
        projects.alphaUser(), "cross-link-alpha-pd", ALPHA_PD_USER_ID, projects.alphaAccount());
    assertUser(projects.betaUser(), "cross-link-beta-pd", BETA_PD_USER_ID, projects.betaAccount());
    assertProjectStatuses(projects);
    assertNoRealCaseUnderBetaNewPoa(projects);
    assertDistinctBetaRealCaseMethodologies(projects);
    assertCreditingPeriods(projects);
    assertCandidateDetailAssociations(projects);

    var mutatedRealCase = reload(projects.betaRealCaseAcm0001());
    mutatedRealCase.setProjectStatus(new ProjectStatus(ProjectStatus.NEW));
    mutatedRealCase.setMethodologies(new ArrayList<>(List.of(new Methodology(2))));
    mutatedRealCase.setProjectTypes(new ArrayList<>());
    projectRepository.save(mutatedRealCase);
    creditingPeriodRepository.deleteAllByProjectId(mutatedRealCase.getId());

    var resetProjects = fixture.populate();

    assertThat(reload(resetProjects.betaRealCaseAcm0001()).getProjectStatus().getId())
        .isEqualTo(ProjectStatus.LISTED);
    assertReferenceIds(reload(resetProjects.betaRealCaseAcm0001()).getMethodologies(), 0);
    assertReferenceIds(reload(resetProjects.betaRealCaseAcm0001()).getProjectTypes(), 4);
    assertThat(
            creditingPeriodRepository.findAllByProjectId(
                resetProjects.betaRealCaseAcm0001().getId()))
        .hasSize(1);
  }

  private static void assertAccount(Account account, String expectedName) {
    assertThat(account.getName()).isEqualTo(expectedName);
    assertThat(account.getEmailAddress()).endsWith("@test");
    assertThat(account.getIsActive()).isTrue();
  }

  private static void assertUser(
      User user, String expectedUserName, UUID expectedIdpId, Account account) {
    assertThat(user.getUserName()).isEqualTo(expectedUserName);
    assertThat(user.getIdpUserId()).isEqualTo(expectedIdpId);
    assertThat(user.getAccount().getId()).isEqualTo(account.getId());
    assertThat(user.isActive()).isTrue();
    assertThat(user.getGroups()).containsExactly(PROJECT_DEVELOPER_ROLE);
  }

  private static void assertProjectStatuses(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    assertThat(projects.betaNewPoaWithoutRealCase().getProjectStatus().getId())
        .isEqualTo(ProjectStatus.NEW);
    assertThat(projects.betaListedPoa().getProjectStatus().getId()).isEqualTo(ProjectStatus.LISTED);
    assertThat(projects.betaDesignCertifiedPoa().getProjectStatus().getId())
        .isEqualTo(ProjectStatus.DESIGN_CERTIFIED);
  }

  private void assertNoRealCaseUnderBetaNewPoa(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    assertThat(
            projectRepository.findAllByAccountIdAndMasterProjectIdAndProjectClassificationIdIn(
                projects.betaAccount().getId(),
                projects.betaNewPoaWithoutRealCase().getId(),
                ProjectClassification.getRealCaseVpaIds()))
        .isEmpty();
  }

  private void assertDistinctBetaRealCaseMethodologies(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var realCases =
        List.of(reload(projects.betaRealCaseAcm0001()), reload(projects.betaRealCaseAcm0002()));

    assertThat(realCases)
        .extracting(project -> project.getProjectClassification().getId())
        .containsOnly(8);
    assertThat(realCases)
        .extracting(project -> project.getMasterProject().getId())
        .containsExactly(
            projects.betaListedPoa().getId(), projects.betaDesignCertifiedPoa().getId());
    assertThat(realCases)
        .flatExtracting(Project::getMethodologies)
        .extracting(ReferenceDataType::getId)
        .containsExactlyInAnyOrder(0, 1);
  }

  private void assertCreditingPeriods(CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    assertThat(
            creditingPeriodRepository.findAllByProjectId(
                projects.betaNewPoaWithoutRealCase().getId()))
        .isEmpty();
    assertThat(creditingPeriodRepository.findAllByProjectId(projects.betaListedPoa().getId()))
        .isEmpty();
    assertThat(
            creditingPeriodRepository.findAllByProjectId(projects.betaDesignCertifiedPoa().getId()))
        .isEmpty();
    assertCreditingPeriod(
        projects.betaRealCaseAcm0001(),
        0,
        ACM0001_CREDITING_PERIOD_START,
        ACM0001_CREDITING_PERIOD_END,
        1.0);
    assertCreditingPeriod(
        projects.betaRealCaseAcm0002(),
        1,
        ACM0002_CREDITING_PERIOD_START,
        ACM0002_CREDITING_PERIOD_END,
        2.0);
  }

  private void assertCreditingPeriod(
      Project project,
      int expectedMethodologyId,
      ZonedDateTime expectedStartDate,
      ZonedDateTime expectedEndDate,
      double expectedVersion) {
    assertThat(creditingPeriodRepository.findAllByProjectId(project.getId()))
        .singleElement()
        .satisfies(
            period -> {
              assertThat(period.getPeriodId()).isEqualTo(1);
              assertThat(period.getStartDate()).isEqualTo(expectedStartDate);
              assertThat(period.getEndDate()).isEqualTo(expectedEndDate);
              assertThat(period.getMethodologies())
                  .singleElement()
                  .satisfies(
                      methodology -> {
                        assertThat(methodology.getMethodologyId()).isEqualTo(expectedMethodologyId);
                        assertThat(methodology.getVersion()).isEqualTo(expectedVersion);
                      });
            });
  }

  private void assertCandidateDetailAssociations(
      CrossLinkingProjectFixture.CrossLinkingProjects projects) {
    var betaProjects =
        List.of(
            reload(projects.betaNewPoaWithoutRealCase()),
            reload(projects.betaListedPoa()),
            reload(projects.betaDesignCertifiedPoa()),
            reload(projects.betaRealCaseAcm0001()),
            reload(projects.betaRealCaseAcm0002()));

    betaProjects.forEach(
        project -> {
          assertReferenceIds(project.getProjectTypes(), 4);
          assertReferenceIds(project.getProjectAssets(), 0);
          assertReferenceIds(project.getProjectLocations(), 2);
          assertThat(project.getSustainableDevelopmentGoals()).isNotEmpty();
          assertThat(project.getCurrentStandardVersion().getId()).isEqualTo(2);
          assertThat(project.getCarbonStream().getId()).isEqualTo(1);
          assertThat(project.getProjectSize().getId()).isEqualTo(1);
          assertThat(project.getRegistrationType().getId()).isEqualTo(0);
        });
  }

  private Project reload(Project project) {
    return projectRepository.findById(project.getId()).orElseThrow();
  }

  private static void assertReferenceIds(
      Collection<? extends ReferenceDataType> references, Integer... ids) {
    assertThat(references).extracting(ReferenceDataType::getId).containsExactly(ids);
  }
}
