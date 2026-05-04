package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.account.AccountFinder;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociatedAccountsServiceTest {

  @InjectMocks AssociatedAccountsService associatedAccountsService;

  @Mock AccountFinder accountFinder;

  @Mock AssignmentFinder assignmentFinder;

  @Mock CompetenciesService competenciesService;

  @Mock StakeholderConsultationFinder stakeholderConsultationFinder;

  @Test
  void findAccountsAssociatedWithRequest() {
    var account = buildAccount();
    var request = buildRequest(account);
    setupInterestedAccountMocks(account, request);

    var actual = associatedAccountsService.findAccountsAssociatedWithRequest(request);

    assertThat(actual).hasSize(4);
  }

  @Test
  void getAllRelevantStakeholderAccountsIfInConsultation() {
    var account = buildAccount();
    var request = buildRequest(account);
    when(stakeholderConsultationFinder.isInConsultation(request)).thenReturn(true);
    when(competenciesService.findAllRelevantStakeholderAccounts(request))
        .thenReturn(List.of(account));

    var actual =
        associatedAccountsService.getAllRelevantStakeholderAccountsIfInConsultation(request);
    assertThat(actual).hasSize(1);
  }

  @Test
  void getAllRelevantStakeholderAccountsIfInConsultation_notInConsultation() {
    var account = buildAccount();
    var request = buildRequest(account);
    when(stakeholderConsultationFinder.isInConsultation(request)).thenReturn(false);

    var actual =
        associatedAccountsService.getAllRelevantStakeholderAccountsIfInConsultation(request);
    verifyNoInteractions(competenciesService);
    assertThat(actual).isEmpty();
  }

  private void setupInterestedAccountMocks(Account account, Request request) {
    when(stakeholderConsultationFinder.isInConsultation(request)).thenReturn(true);
    when(accountFinder.getArmTeamAccount()).thenReturn(account);
    when(accountFinder.forceGetAccountById(request.getProject().getAccountId()))
        .thenReturn(account);
    when(assignmentFinder.findAssignedAccountsByRequestId(request.getId(), Collections.emptyList()))
        .thenReturn(List.of(account, account));
  }

  private static Account buildAccount() {
    var account = new Account();
    account.setId(10);
    account.setName("test");
    return account;
  }

  private static Request buildRequest(Account account) {
    var project = new Project();
    project.setAccountId(account.getId());
    return Request.builder().id(1).project(project).build();
  }
}
