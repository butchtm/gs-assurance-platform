package org.goldstandard.assurance.api.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.MonitoringPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.lookup.RequestStatus;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestFactory {
  private static final int DOCUMENTS_MISSING_STATUS = 0;

  private final Clock clock;
  private final RequestFinder requestFinder;
  private final ProjectFinder projectFinder;
  private final RequestNameCalculator nameCalculator;
  private final AuthenticatedUserSupplier authenticatedUserSupplier;
  private final RequestMethodologyFactory methodologyFactory;
  private final RequestDraftCreditingPeriodFactory draftCreditingPeriodFactory;
  private final RequestDraftMonitoringPeriodFactory draftMonitoringPeriodFactory;

  public Request buildRequest(int projectId, RequestCreateUpdateDTO create) {
    Request request =
        populateRequest(
            projectId,
            create.getCreditingPeriod(),
            create.getMonitoringPeriod(),
            create.getRequestTypeId(),
            create.getEmissionReductionAmount(),
            create.getMethodologies());
    request.setStartDate(create.getStartDate());
    return request;
  }

  public Request buildRequestForBatch(
      BatchRequestCreateDTO create, BatchRequestCreateDTO.ProjectRequestInfo projectRequestInfo) {
    return populateRequest(
        projectRequestInfo.getProjectId(),
        projectRequestInfo.getCreditingPeriod(),
        projectRequestInfo.getMonitoringPeriod(),
        create.getRequestTypeId(),
        projectRequestInfo.getEmissionReductionAmount(),
        projectRequestInfo.getMethodologies());
  }

  private Request populateRequest(
      int projectId,
      CreditingPeriodDTO creditingPeriod,
      MonitoringPeriodDTO monitoringPeriod,
      int requestTypeId,
      Integer annualEmissionReduction,
      Collection<RequestMethodologyDTO> methodologies) {
    var project = projectFinder.forceGetProjectById(projectId);
    var requests = requestFinder.getByProjectId(projectId);
    int requestCountByType = countRequestsByType(requests, requestTypeId);

    Request request = new Request();
    request.setProject(project);
    request.setRequestName(nameCalculator.calculateName(requestTypeId, requestCountByType));
    request.setRequestType(new RequestType(requestTypeId));
    request.setIndex(calculateNextIndex(requests));
    request.setSameRequestTypeCount(requestCountByType);
    request.setEmissionReductionAmount(annualEmissionReduction);
    request.setCreditingPeriod(
        Optional.ofNullable(creditingPeriod).map(CreditingPeriodDTO::getPeriodId).orElse(null));
    request.setMonitoringPeriodId(
        Optional.ofNullable(monitoringPeriod).map(MonitoringPeriodDTO::getId).orElse(null));
    draftMonitoringPeriodFactory
        .build(request, monitoringPeriod)
        .ifPresent(request::setDraftMonitoringPeriod);
    request.setStartDate(ZonedDateTime.now(clock));
    request.setCurrentRequestStatus(RequestStatus.inPreparation());
    request.setRequestDocumentStatus(DOCUMENTS_MISSING_STATUS);
    request.setUpdatedByUserId(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId());
    request.setManuallyAdded(true);
    request.setRequestMethodologies(methodologyFactory.toMethodologies(methodologies, request));
    request.setMigrated(false);
    request.setDeltaExcluded(false);
    draftCreditingPeriodFactory.build(creditingPeriod).ifPresent(request::setDraftCreditingPeriod);
    return request;
  }

  private int calculateNextIndex(List<Request> requests) {
    int lastIndex = requests.stream().mapToInt(Request::getIndex).max().orElse(-1);
    return lastIndex + 1;
  }

  private int countRequestsByType(List<Request> requests, int requestTypeId) {
    return Math.toIntExact(
        requests.stream().filter(r -> r.getRequestType().getId() == requestTypeId).count());
  }
}
