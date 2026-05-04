package org.goldstandard.assurance.api.service.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountConsultationsGrouper {

  private final CompetenciesService competenciesService;

  public Map<Account, Collection<StakeholderConsultation>> groupConsultationsByAccount(
      Collection<StakeholderConsultation> consultations) {
    return consultations.stream()
        .flatMap(
            consultation ->
                competenciesService
                    .findAllRelevantStakeholderAccounts(consultation.getRequest())
                    .stream()
                    .map(account -> Map.entry(account, consultation)))
        .collect(
            Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toCollection(ArrayList::new))));
  }
}
