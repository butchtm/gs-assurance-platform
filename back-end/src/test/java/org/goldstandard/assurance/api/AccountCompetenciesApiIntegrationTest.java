package org.goldstandard.assurance.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import java.util.List;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AccountCompetenciesApiIntegrationTest extends AbstractApiTest {

  private static final String ENDPOINT_TO_TEST = "/accounts/%d/competencies";
  private static final int VVB_ACCOUNT_ID = 1;

  @Autowired private AccountRepository accountRepository;

  @Autowired private CompetenciesRepository competenciesRepository;

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

    var response = sendRequestToCompetenciesEndpoint(competenciesDto, accountId, HttpMethod.POST);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var competenciesId = response.getBody();
    var account = accountRepository.findById(accountId).orElseThrow();
    var savedCompetencies = competenciesRepository.findById(competenciesId).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, competenciesDto);
  }

  @Test
  void shouldUpdateCompetenciesToVVBAccount() {
    var accountId = VVB_ACCOUNT_ID;

    var initialCompetencies = CompetenciesDTOMother.preFilled();
    sendRequestToCompetenciesEndpoint(initialCompetencies, accountId, HttpMethod.POST);

    var competenciesDto = CompetenciesDTOMother.preFilled();
    competenciesDto.setRequestTypeIds(List.of(1001));
    competenciesDto.setProjectGsids(List.of("GS2"));

    var response = sendRequestToCompetenciesEndpoint(competenciesDto, accountId, HttpMethod.PUT);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var competenciesId = response.getBody();
    var account = accountRepository.findById(accountId).orElseThrow();
    var savedCompetencies = competenciesRepository.findById(competenciesId).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, competenciesDto);
  }

  @Test
  void shouldThrowBadRequestExceptionWithInvalidDto() {
    var competenciesDto = CompetenciesDTOMother.empty();

    var response = postRequestToCompetenciesEndpointToCheckException(competenciesDto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void shouldGetCompetenciesForVVBAccount() {
    var accountId = VVB_ACCOUNT_ID;

    var initialCompetencies = CompetenciesDTOMother.preFilled();
    var initialResponse =
        sendRequestToCompetenciesEndpoint(initialCompetencies, accountId, HttpMethod.POST);
    assertThat(initialResponse.getBody()).isNotNull();
    var response = getRequestCompetenciesEndpoint();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var returnedDto = response.getBody();

    var account = accountRepository.findById(accountId).orElseThrow();
    var savedCompetencies =
        competenciesRepository.findById(initialResponse.getBody()).orElseThrow();
    assertSavedCompetenciesConsistency(account, savedCompetencies, returnedDto);
  }

  private ResponseEntity<Integer> sendRequestToCompetenciesEndpoint(
      CompetenciesDTO competenciesDto, int accountId, HttpMethod method) {
    return this.restTemplate.exchange(
        baseUrl + String.format(ENDPOINT_TO_TEST, accountId),
        method,
        getHttpEntityWithTokenAndBodyForRole(competenciesDto, ADMIN_ROLE),
        Integer.class);
  }

  private ResponseEntity<String> postRequestToCompetenciesEndpointToCheckException(
      CompetenciesDTO competenciesDto) {
    return this.restTemplate.exchange(
        baseUrl + String.format(ENDPOINT_TO_TEST, VVB_ACCOUNT_ID),
        HttpMethod.POST,
        getHttpEntityWithTokenAndBodyForRole(competenciesDto, ADMIN_ROLE),
        String.class);
  }

  private ResponseEntity<CompetenciesDTO> getRequestCompetenciesEndpoint() {
    return this.restTemplate.exchange(
        baseUrl + String.format(ENDPOINT_TO_TEST, VVB_ACCOUNT_ID),
        HttpMethod.GET,
        getHttpEntityWithTokenForRole(ADMIN_ROLE),
        CompetenciesDTO.class);
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
