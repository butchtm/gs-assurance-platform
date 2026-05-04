package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StakeholderConsultationCompleterTest {

  @InjectMocks private StakeholderConsultationCompleter completer;

  @Mock private StakeholderConsultationFinder consultationFinder;

  @Mock private Clock clock;

  @Mock private CompleteConsultationTransaction transaction;

  @Captor private ArgumentCaptor<List<StakeholderConsultation>> captor;

  @Test
  void shouldCompleteAllLiveStakeholderConsultations() {
    var requestId = 1;
    StakeholderConsultation consultation1 = new StakeholderConsultation();
    StakeholderConsultation consultation2 = new StakeholderConsultation();
    when(consultationFinder.getLiveStakeholderConsultations(requestId))
        .thenReturn(List.of(consultation1, consultation2));
    ZonedDateTime now = mockTimeNow();

    completer.completeLiveRequestStakeholderConsultations(requestId);

    verify(transaction).performComplete(eq(requestId), captor.capture());
    List<StakeholderConsultation> savedConsultations = captor.getValue();
    assertThat(savedConsultations).containsExactly(consultation1, consultation2);
    assertThat(savedConsultations)
        .map(StakeholderConsultation::getEndDate)
        .containsExactly(now, now);
  }

  private ZonedDateTime mockTimeNow() {
    ZonedDateTime now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }
}
