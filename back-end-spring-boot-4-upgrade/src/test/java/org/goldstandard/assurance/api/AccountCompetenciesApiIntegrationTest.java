package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Competencies;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectAsset;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectSize;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.entity.lookup.SustainableDevelopmentGoal;
import org.goldstandard.assurance.api.fixtures.CompetenciesDTOMother;
import org.goldstandard.assurance.api.repository.AccountRepository;
import org.goldstandard.assurance.api.repository.CompetenciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AccountCompetenciesApiIntegrationTest extends AbstractApiTest {

  private static final int VVB_ACCOUNT_ID = 1;

  @Autowired ApiClient client;
  @Autowired AccountRepository accountRepository;

  @Autowired CompetenciesRepository competenciesRepository;

  @BeforeEach
  void clearVvbCompetencies() {
    var vvb = accountRepository.findById(VVB_ACCOUNT_ID).orElseThrow();
    vvb.setCompetencies(null);
    accountRepository.save(vvb);
    competenciesRepository.deleteAll();
  }

  @Test
  void shouldSaveNewCompetenciesToVVBAccount() {
    var accountId = VVB_ACCOUNT_ID;
    var competenciesDto = CompetenciesDTOMother.preFilled();

    var response = client.createCompetencies(accountId, competenciesDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var competenciesId = response.getBody();
    assertThat(competenciesId).isNotNull().isPositive();
    var account = accountRepository.findById(accountId).orElseThrow();
    var savedCompetencies = competenciesRepository.findById(competenciesId).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, competenciesDto);
  }

  @Test
  void shouldUpdateCompetenciesToVVBAccount() {
    var accountId = VVB_ACCOUNT_ID;
    var initialCompetencies = CompetenciesDTOMother.preFilled();
    client.createCompetencies(accountId, initialCompetencies);
    var competenciesDto = CompetenciesDTOMother.preFilled();
    competenciesDto.setRequestTypeIds(List.of(1001));
    competenciesDto.setProjectGsids(List.of("GS2"));

    var response = client.updateCompetencies(accountId, competenciesDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var competenciesId = response.getBody();
    assertThat(competenciesId).isNotNull().isPositive();
    var account = accountRepository.findById(accountId).orElseThrow();
    var savedCompetencies = competenciesRepository.findById(competenciesId).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, competenciesDto);
  }

  @Test
  void shouldThrowBadRequestExceptionWithInvalidDto() {
    var competenciesDto = CompetenciesDTOMother.empty();

    var response = client.createCompetencies(VVB_ACCOUNT_ID, competenciesDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldGetCompetenciesForVVBAccount() {
    var initialCompetencies = CompetenciesDTOMother.preFilled();
    var initialResponse = client.createCompetencies(VVB_ACCOUNT_ID, initialCompetencies);
    var competenciesId = Objects.requireNonNull(initialResponse.getBody());

    var response = client.getCompetencies(VVB_ACCOUNT_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var returnedDto = response.getBody();
    assertThat(returnedDto).isNotNull();
    var account = accountRepository.findById(VVB_ACCOUNT_ID).orElseThrow();
    var savedCompetencies = competenciesRepository.findById(competenciesId).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, returnedDto);
  }

  private static void assertSavedCompetenciesConsistency(
      Account account, Competencies savedCompetencies, CompetenciesDTO competenciesDto) {
    assertThat(account.getCompetencies().getId()).isSameAs(savedCompetencies.getId());
    assertThat(account.getCompetencies().getEstimatedAnnualEmissionReductionAmount())
        .isNotNull()
        .isEqualTo(savedCompetencies.getEstimatedAnnualEmissionReductionAmount())
        .isEqualTo(competenciesDto.getEstimatedAnnualEmissionReductionAmount());
    assertThat(account.getCompetencies().getProjectGsids())
        .isNotNull()
        .hasSameElementsAs(savedCompetencies.getProjectGsids())
        .hasSameElementsAs(competenciesDto.getProjectGsids());
    assertThat(
            account.getCompetencies().getMethodologies().stream().map(Methodology::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getMethodologies().stream().map(Methodology::getId).toList())
        .isEqualTo(competenciesDto.getMethodologyIds());
    assertThat(
            account.getCompetencies().getCertifiedProducts().stream()
                .map(ProjectAsset::getId)
                .toList())
        .isNotNull()
        .isEqualTo(
            savedCompetencies.getCertifiedProducts().stream().map(ProjectAsset::getId).toList())
        .isEqualTo(competenciesDto.getCertifiedProductIds());
    assertThat(
            account.getCompetencies().getProjectDevelopers().stream().map(Account::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getProjectDevelopers().stream().map(Account::getId).toList())
        .isEqualTo(competenciesDto.getProjectDeveloperIds());
    assertThat(
            account.getCompetencies().getProjectLocations().stream()
                .map(ProjectLocation::getId)
                .toList())
        .isNotNull()
        .isEqualTo(
            savedCompetencies.getProjectLocations().stream().map(ProjectLocation::getId).toList())
        .isEqualTo(competenciesDto.getProjectLocationIds());
    assertThat(
            account.getCompetencies().getProjectScales().stream().map(ProjectSize::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getProjectScales().stream().map(ProjectSize::getId).toList())
        .isEqualTo(competenciesDto.getProjectScaleIds());
    assertThat(
            account.getCompetencies().getProjectTypes().stream().map(ProjectType::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getProjectTypes().stream().map(ProjectType::getId).toList())
        .isEqualTo(competenciesDto.getProjectTypeIds());
    assertThat(
            account.getCompetencies().getRequestTypes().stream().map(RequestType::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getRequestTypes().stream().map(RequestType::getId).toList())
        .isEqualTo(competenciesDto.getRequestTypeIds());
    assertThat(account.getCompetencies().getVvbsAssigned().stream().map(Account::getId).toList())
        .isNotNull()
        .isEqualTo(savedCompetencies.getVvbsAssigned().stream().map(Account::getId).toList())
        .isEqualTo(competenciesDto.getVvbAssignedIds());
    assertThat(
            account.getCompetencies().getSustainableDevelopmentGoals().stream()
                .map(SustainableDevelopmentGoal::getId)
                .toList())
        .isNotNull()
        .isEqualTo(
            savedCompetencies.getSustainableDevelopmentGoals().stream()
                .map(SustainableDevelopmentGoal::getId)
                .toList())
        .isEqualTo(competenciesDto.getSustainableDevelopmentGoalIds());
  }
}
