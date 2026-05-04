package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.service.AuditEventSubject.CONSULTATION_COMPLETE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompleteConsultationTransactionTest {

  @InjectMocks private CompleteConsultationTransaction transaction;

  @Mock private StakeholderConsultationRepository consultationRepository;

  @Mock private ConsultationAuditInfoFactory consultationAuditInfoFactory;

  @Mock private AuditService auditService;

  @Captor private ArgumentCaptor<List<StakeholderConsultation>> consultationsCaptor;

  @Captor private ArgumentCaptor<Map<String, Object>> mapCaptor;

  @Test
  void shouldSaveAllConsultations() {
    int requestId = 1;
    var consultations = List.of(new StakeholderConsultation());
    var savedConsultations = List.of(mock(StakeholderConsultation.class));
    when(consultationRepository.saveAll(consultations)).thenReturn(savedConsultations);

    transaction.performComplete(requestId, consultations);

    verify(consultationRepository).saveAll(consultationsCaptor.capture());
    assertThat(consultationsCaptor.getValue()).isEqualTo(consultations);
  }

  @Test
  void shouldAuditConsultations() {
    var requestId = 2;
    var consultations = List.of(new StakeholderConsultation());
    var savedConsultations = List.of(mock(StakeholderConsultation.class));
    when(consultationRepository.saveAll(consultations)).thenReturn(savedConsultations);
    Map<String, Object> expectedAuditInfo =
        givenConsultationAuditInfo(requestId, savedConsultations);

    transaction.performComplete(requestId, consultations);

    verify(auditService).auditEvent(eq(CONSULTATION_COMPLETE), mapCaptor.capture());
    assertThat(mapCaptor.getValue()).isEqualTo(expectedAuditInfo);
  }

  private Map<String, Object> givenConsultationAuditInfo(
      int requestId, Collection<StakeholderConsultation> consultations) {
    Map<String, Object> info = Map.of("key", "value");
    when(consultationAuditInfoFactory.build(requestId, consultations)).thenReturn(info);
    return info;
  }
}
