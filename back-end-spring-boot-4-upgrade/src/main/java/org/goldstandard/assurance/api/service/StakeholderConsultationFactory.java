package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.repository.ConsultationTypeRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakeholderConsultationFactory {

  private final Clock clock;
  private final ConsultationTypeRepository consultationTypeRepository;
  private final WeekdayCalculator weekdayCalculator;
  private final StakeholderConsultationFinder finder;

  public List<StakeholderConsultation> buildConsultations(Request request) {
    if (finder.isInConsultation(request)) {
      log.info(
          "request with id [{}] is already in consultation so returning empty list",
          request.getId());
      return Collections.emptyList();
    }
    return createConsultationsOfAllTypes(request);
  }

  private List<StakeholderConsultation> createConsultationsOfAllTypes(Request request) {
    ZonedDateTime startDate = ZonedDateTime.now(clock);

    return consultationTypeRepository.findAll().stream()
        .map(type -> toConsultation(type, startDate, request))
        .toList();
  }

  private StakeholderConsultation toConsultation(
      ConsultationType type, ZonedDateTime startDate, Request request) {
    var endDate = weekdayCalculator.addWeekdays(startDate, type.getWorkingDays());
    log.info(
        "creating stakeholder consultation for request {} of type {} starting at {} and ending at {}",
        request.getId(),
        type,
        startDate,
        endDate);
    return StakeholderConsultation.builder()
        .consultationType(type)
        .project(request.getProject())
        .request(request)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }
}
