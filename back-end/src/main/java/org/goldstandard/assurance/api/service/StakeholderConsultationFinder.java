package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.ConsultationFiltersDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.view.BatchParentIdView;
import org.goldstandard.assurance.api.repository.BatchParentIdViewRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StakeholderConsultationFinder {

  private final Clock clock;
  private final StakeholderConsultationRepository repository;
  private final StakeholderConsultationSpecFactory specFactory;
  private final BatchParentIdViewRepository batchParentRepository;

  public boolean isInConsultation(Integer requestId) {
    return !getLiveStakeholderConsultations(requestId).isEmpty();
  }

  public boolean isInConsultation(Request request) {
    return isInConsultation(request.getId());
  }

  public Optional<StakeholderConsultation> getPrivateConsultation(Request request) {
    var consultations = getLiveStakeholderConsultations(request.getId());
    var consultation =
        consultations.stream()
            .filter(
                result -> Objects.equals(result.getProject().getId(), request.getProject().getId()))
            .filter(result -> result.getConsultationType().isTac())
            .findFirst();
    log.debug("found private consultation {} for request id {}", consultation, request.getId());
    return consultation;
  }

  public List<StakeholderConsultation> getLiveStakeholderConsultations(int requestId) {
    var now = ZonedDateTime.now(clock);
    var lookupRequestId = toLookupRequestId(requestId);
    var consultations = repository.getAllInConsultationByRequestId(lookupRequestId, now);
    log.debug(
        "got {} live stakeholder consultations at {} for request {} using lookup request id {}",
        consultations.size(),
        now,
        requestId,
        lookupRequestId);
    return consultations;
  }

  public List<StakeholderConsultation> getLiveUnnotifiedStakeholderConsultations() {
    var now = ZonedDateTime.now(clock);
    var consultations = repository.getAllUnnotifiedInConsultation(now);
    log.debug(
        "got {} live stakeholder consultations started before and ending after {}",
        consultations.size(),
        now);
    return consultations;
  }

  public List<StakeholderConsultation> getStakeholderConsultations(int requestId) {
    var lookupRequestId = toLookupRequestId(requestId);
    var consultations = repository.getAllByRequestId(lookupRequestId);
    log.debug(
        "got {} stakeholder consultations for request {} using lookup request id {}",
        consultations.size(),
        requestId,
        lookupRequestId);
    return consultations;
  }

  private Integer toLookupRequestId(Integer requestId) {
    return batchParentRepository
        .getBatchParentIdView(requestId)
        .map(BatchParentIdView::getParentRequestId)
        .orElse(requestId);
  }

  public Page<StakeholderConsultation> getLiveStakeholderConsultations(
      ConsultationFiltersDTO filters, Pageable pageable) {
    var spec = specFactory.toSpec(filters);
    return repository.findAll(spec, pageable);
  }
}
