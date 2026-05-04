package org.goldstandard.assurance.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StakeholderConsultationService {
  private final StakeholderConsultationFinder finder;
  private final StakeholderConsultationRepository repository;

  public List<StakeholderConsultation> getLiveUnnotifiedStakeholderConsultations() {
    return finder.getLiveUnnotifiedStakeholderConsultations();
  }

  public void markNotificationsSent(List<StakeholderConsultation> consultations) {
    var consultationIds = consultations.stream().map(StakeholderConsultation::getId).toList();
    repository.markAsNotified(consultationIds);
  }
}
