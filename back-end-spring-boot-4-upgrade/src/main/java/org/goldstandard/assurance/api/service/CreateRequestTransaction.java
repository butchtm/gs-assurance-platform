package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_CREATE;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.repository.RequestCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.RequestMonitoringPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateRequestTransaction {

  private final RequestRepository requestRepository;
  private final RequestMethodologyRepository requestMethodologyRepository;
  private final AuditService auditService;
  private final RequestCreditingPeriodRepository requestCreditingPeriodRepository;
  private final RequestMonitoringPeriodRepository requestMonitoringPeriodRepository;

  @Transactional
  public Request performCreate(Request request) {
    var methodologies = request.getRequestMethodologies();
    request.setRequestMethodologies(Collections.emptyList());
    var draftCreditingPeriod = request.getDraftCreditingPeriod();
    if (draftCreditingPeriod.isPresent()) {
      request.setDraftCreditingPeriod(null);
    }
    var draftMonitoringPeriod = request.getDraftMonitoringPeriod();
    if (draftMonitoringPeriod.isPresent()) {
      request.setDraftMonitoringPeriod(null);
    }
    var savedRequest = requestRepository.save(request);
    draftCreditingPeriod
        .map(period -> period.withRequestId(savedRequest.getId()))
        .ifPresent(requestCreditingPeriodRepository::save);
    draftMonitoringPeriod
        .map(period -> period.withRequestId(savedRequest.getId()))
        .ifPresent(requestMonitoringPeriodRepository::save);
    var updatedMethodologies =
        methodologies.stream().map(m -> m.withRequest(savedRequest)).toList();
    requestMethodologyRepository.saveAll(updatedMethodologies);
    auditService.auditEvent(REQUEST_CREATE, createRequestAuditInformation(savedRequest));
    savedRequest.setRequestMethodologies(updatedMethodologies);
    return savedRequest;
  }

  private static Map<String, Object> createRequestAuditInformation(Request request) {
    return Map.ofEntries(
        Map.entry("projectId", request.getProject().getId()),
        Map.entry("requestId", request.getId()));
  }
}
