package org.goldstandard.assurance.api.service.account.competencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.converter.CompetenciesConverter;
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
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.fixtures.CompetenciesDTOMother;
import org.goldstandard.assurance.api.fixtures.CreateCompetenciesEntityMother;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetenciesServiceTest {

  @InjectMocks CompetenciesService competenciesService;

  @Mock AccountFinder accountFinder;

  @Mock CompetenciesValidator competenciesValidator;

  @Spy CompetenciesConverter mapper;

  @Mock CompetenciesTransaction transaction;

  @Mock CompetenciesFinder competenciesFinder;

  @Captor ArgumentCaptor<Competencies> competenciesArgumentCaptor;

  @Test
  void shouldMapDtoToEntityForTransactionOnSave() {
    var competenciesDto = CompetenciesDTOMother.preFilled();
    var account = createAccount();
    when(accountFinder.forceGetAccountById(account.getId())).thenReturn(account);
    doCallRealMethod().when(mapper).toEntity(any(Competencies.class), eq(competenciesDto));

    competenciesService.createCompetencies(account.getId(), competenciesDto);
    verify(transaction).create(competenciesArgumentCaptor.capture(), eq(account));

    var competencies = competenciesArgumentCaptor.getValue();
    assertEntityAgainstDto(competencies, competenciesDto);
  }

  @Test
  void shouldMapDtoToEntityForTransactionOnUpdate() {
    var competencies = CreateCompetenciesEntityMother.createEmptyCompetencies();
    competencies.setProjectGsids(List.of("GS1"));
    competencies.setRequestTypes(List.of(new RequestType(1000)));

    var account = createAccount();
    account.setCompetencies(competencies);

    var updateDto = CompetenciesDTOMother.empty();
    updateDto.setProjectGsids(List.of("GS2"));
    updateDto.setRequestTypeIds(List.of(1001));

    when(accountFinder.forceGetAccountById(account.getId())).thenReturn(account);
    doCallRealMethod().when(mapper).toEntity(any(Competencies.class), eq(updateDto));

    competenciesService.updateCompetencies(account.getId(), updateDto);
    verify(transaction).update(competenciesArgumentCaptor.capture(), eq(account));

    var updatedCompetencies = competenciesArgumentCaptor.getValue();
    assertEntityAgainstDto(updatedCompetencies, updateDto);
  }

  @Test
  void shouldThrowWhenAccountNotFound() {
    var competenciesDto = CompetenciesDTOMother.empty();
    var account = createAccount();
    var expectedError = new DataException("Account not found.");

    doThrow(expectedError).when(accountFinder).forceGetAccountById(account.getId());
    Throwable error =
        catchThrowable(
            () -> competenciesService.createCompetencies(account.getId(), competenciesDto));
    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(competenciesValidator);
    verifyNoInteractions(mapper);
    verifyNoInteractions(transaction);
  }

  @Test
  void shouldThrowWhenValidationFails() {
    var competenciesDto = CompetenciesDTOMother.empty();
    var account = createAccount();
    var expectedError = new DataException("Validation failed..");
    when(accountFinder.forceGetAccountById(account.getId())).thenReturn(account);

    doThrow(expectedError).when(competenciesValidator).validateNew(competenciesDto, account);
    Throwable error =
        catchThrowable(
            () -> competenciesService.createCompetencies(account.getId(), competenciesDto));
    assertThat(error).isEqualTo(expectedError);
    verifyNoInteractions(mapper);
    verifyNoInteractions(transaction);
  }

  @Test
  void shouldReturnDtoWithCorrectValues() {
    var competencies = CreateCompetenciesEntityMother.createPreFilledCompetencies();
    var account = createAccount();
    when(competenciesFinder.getCompetencyByAccountId(account.getId()))
        .thenReturn(Optional.of(competencies));

    var result = competenciesService.getCompetencyByAccountId(account.getId());

    assertThat(result).isPresent();
    assertEntityAgainstDto(competencies, result.get());
  }

  private Account createAccount() {
    return new Account(11);
  }

  private static void assertEntityAgainstDto(
      Competencies competenciesEntity, CompetenciesDTO competenciesDto) {
    assertThat(competenciesEntity.getEstimatedAnnualEmissionReductionAmount())
        .isSameAs(competenciesDto.getEstimatedAnnualEmissionReductionAmount());
    assertThat(competenciesEntity.getProjectGsids()).isSameAs(competenciesDto.getProjectGsids());
    assertThat(competenciesEntity.getMethodologies().stream().map(Methodology::getId).toList())
        .isEqualTo(competenciesDto.getMethodologyIds());
    assertThat(competenciesEntity.getCertifiedProducts().stream().map(ProjectAsset::getId).toList())
        .isEqualTo(competenciesDto.getCertifiedProductIds());
    assertThat(competenciesEntity.getProjectDevelopers().stream().map(Account::getId).toList())
        .isEqualTo(competenciesDto.getProjectDeveloperIds());
    assertThat(
            competenciesEntity.getProjectLocations().stream().map(ProjectLocation::getId).toList())
        .isEqualTo(competenciesDto.getProjectLocationIds());
    assertThat(competenciesEntity.getProjectScales().stream().map(ProjectSize::getId).toList())
        .isEqualTo(competenciesDto.getProjectScaleIds());
    assertThat(competenciesEntity.getProjectTypes().stream().map(ProjectType::getId).toList())
        .isEqualTo(competenciesDto.getProjectTypeIds());
    assertThat(competenciesEntity.getRequestTypes().stream().map(RequestType::getId).toList())
        .isEqualTo(competenciesDto.getRequestTypeIds());
    assertThat(competenciesEntity.getVvbsAssigned().stream().map(Account::getId).toList())
        .isEqualTo(competenciesDto.getVvbAssignedIds());
    assertThat(
            competenciesEntity.getSustainableDevelopmentGoals().stream()
                .map(SustainableDevelopmentGoal::getId)
                .toList())
        .isEqualTo(competenciesDto.getSustainableDevelopmentGoalIds());
  }
}
