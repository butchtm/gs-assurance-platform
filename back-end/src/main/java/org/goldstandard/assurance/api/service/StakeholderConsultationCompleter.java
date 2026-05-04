package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StakeholderConsultationCompleter {

  private final StakeholderConsultationFinder consultationFinder;
  private final Clock clock;
  private final CompleteConsultationTransaction transaction;

  public void completeLiveRequestStakeholderConsultations(int requestId) {
    var consultations = consultationFinder.getLiveStakeholderConsultations(requestId);
    if (consultations.isEmpty()) {
      log.info("no live consultations found to returning without completing");
      return;
    }
    consultations.forEach(consultation -> consultation.setEndDate(ZonedDateTime.now(clock)));
    transaction.performComplete(requestId, consultations);
  }
}
