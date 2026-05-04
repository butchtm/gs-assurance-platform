package org.goldstandard.assurance.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountConsultationsGrouperTest {
  @InjectMocks AccountConsultationsGrouper aggregator;
  @Mock CompetenciesService competenciesService;

  @Test
  void shouldGroupConsultationsUnderEachRelevantStakeholderAccount() {
    Account account1 = buildAccount(1, "Account1");
    Account account2 = buildAccount(2, "Account2");

    Request request1 = buildRequest(1);
    Request request2 = buildRequest(2);

    StakeholderConsultation consultation1 = buildConsultation(request1);
    StakeholderConsultation consultation2 = buildConsultation(request2);

    when(competenciesService.findAllRelevantStakeholderAccounts(request1))
        .thenReturn(List.of(account1));
    when(competenciesService.findAllRelevantStakeholderAccounts(request2))
        .thenReturn(List.of(account2));

    var result = aggregator.groupConsultationsByAccount(List.of(consultation1, consultation2));

    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(account1)).contains(consultation1);
    assertThat(result.get(account2)).contains(consultation2);
  }

  @Test
  void shouldGroupSingleConsultationUnderEachRelevantAccountWhenItBelongsToMultipleAccounts() {
    Account account1 = buildAccount(1, "Account1");
    Account account2 = buildAccount(2, "Account2");

    Request request1 = buildRequest(1);

    StakeholderConsultation consultation1 = buildConsultation(request1);

    when(competenciesService.findAllRelevantStakeholderAccounts(request1))
        .thenReturn(List.of(account1, account2));

    var result = aggregator.groupConsultationsByAccount(List.of(consultation1));

    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(account1)).contains(consultation1);
    assertThat(result.get(account2)).contains(consultation1);
  }

  @Test
  void
      shouldGroupMultipleConsultationsUnderSingleRelevantAccountWhenMultipleBelongToASingleAccount() {
    Account account1 = buildAccount(1, "Account1");

    Request request1 = buildRequest(1);
    Request request2 = buildRequest(2);

    StakeholderConsultation consultation1 = buildConsultation(request1);
    StakeholderConsultation consultation2 = buildConsultation(request2);

    when(competenciesService.findAllRelevantStakeholderAccounts(request1))
        .thenReturn(List.of(account1));
    when(competenciesService.findAllRelevantStakeholderAccounts(request2))
        .thenReturn(List.of(account1));

    var result = aggregator.groupConsultationsByAccount(List.of(consultation1, consultation2));

    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(account1)).contains(consultation1, consultation2);
  }

  private static Account buildAccount(int id, String name) {
    return Account.builder().id(id).name(name).build();
  }

  private static Request buildRequest(int id) {
    return Request.builder().id(id).build();
  }

  private static StakeholderConsultation buildConsultation(Request request) {
    return StakeholderConsultation.builder().request(request).build();
  }
}
