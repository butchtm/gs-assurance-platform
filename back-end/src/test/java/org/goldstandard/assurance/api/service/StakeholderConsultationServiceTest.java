package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StakeholderConsultationServiceTest {

  @InjectMocks StakeholderConsultationService service;
  @Mock StakeholderConsultationFinder finder;
  @Mock StakeholderConsultationRepository repository;

  @Test
  void shouldUseFinderToReturnUnnotifiedLiveStakeholderConsultations() {
    var expectedConsultations = List.of(mock(StakeholderConsultation.class));
    when(finder.getLiveUnnotifiedStakeholderConsultations()).thenReturn(expectedConsultations);

    var consultations = service.getLiveUnnotifiedStakeholderConsultations();

    assertThat(consultations).isEqualTo(expectedConsultations);
  }

  @Test
  void shouldMarkAllProvidedConsultationRecordsAsNotificationSent() {
    var consultations = List.of(buildConsultation(1), buildConsultation(2));

    service.markNotificationsSent(consultations);

    verify(repository).markAsNotified(List.of(1, 2));
  }

  private StakeholderConsultation buildConsultation(int id) {
    return StakeholderConsultation.builder().id(id).build();
  }
}
