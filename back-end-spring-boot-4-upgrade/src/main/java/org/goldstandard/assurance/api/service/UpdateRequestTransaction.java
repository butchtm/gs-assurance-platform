package org.goldstandard.assurance.api.service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.repository.ProjectRepository;
import org.goldstandard.assurance.api.repository.RequestCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.RequestMonitoringPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.RequestRoundRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.goldstandard.assurance.api.repository.activity.RequestActivityRepository;
import org.goldstandard.assurance.api.service.conversation.ConversationTransaction;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateRequestTransaction {

  private final RequestRepository requestRepository;
  private final RequestMethodologyRepository requestMethodologyRepository;
  private final StakeholderConsultationRepository consultationRepository;
  private final AuditService auditService;
  private final RequestAuditInfoFactory requestAuditInfoFactory;
  private final ConsultationAuditInfoFactory consultationAuditInfoFactory;
  private final ProjectRepository projectRepository;
  private final RequestActivityRepository requestActivityRepository;
  private final RequestRoundRepository requestRoundRepository;
  private final CreditingPeriodTransaction creditingPeriodTransaction;
  private final RequestCreditingPeriodRepository requestCreditingPeriodRepository;
  private final RequestMonitoringPeriodRepository requestMonitoringPeriodRepository;
  private final PiiMetadataService piiMetadataService;
  private final ConversationTransaction conversationTransaction;

  @Transactional
  public void updateRequest(RequestUpdateParams params) {
    saveActivities(params.getActivities());
    saveRounds(params.getRounds());
    saveRequests(params);
    saveProjects(params);
    saveConversation(params);
  }

  private void saveRequests(RequestUpdateParams params) {
    saveMethodologies(params);
    var updatedRequest = params.getUpdatedRequest();
    if (!updatedRequest.hasDraftCreditingPeriod()) {
      requestCreditingPeriodRepository.deleteById(updatedRequest.getId());
    }
    if (!updatedRequest.hasDraftMonitoringPeriod()) {
      requestMonitoringPeriodRepository.deleteById(updatedRequest.getId());
    }
    var savedRequest = requestRepository.save(updatedRequest);
    var savedRequests =
        IterableUtils.toList(requestRepository.saveAll(params.getUpdatedChildRequests()));
    auditRequestUpdates(savedRequest, savedRequests);
    var consultations = params.getConsultations();
    if (!consultations.isEmpty()) {
      saveConsultations(savedRequest.getId(), consultations);
    }
  }

  private void saveMethodologies(RequestUpdateParams params) {
    requestMethodologyRepository.deleteAll(params.getOriginalMethodologies());
    requestMethodologyRepository.flush();
    requestMethodologyRepository.saveAll(params.getUpdatedMethodologies());
  }

  private void auditRequestUpdates(Request request, Collection<Request> childRequests) {
    var auditInfo = requestAuditInfoFactory.buildRequestInfo(request, childRequests);
    auditService.auditEvent(AuditEventSubject.REQUEST_UPDATE, auditInfo);
  }

  private void saveConsultations(int requestId, Collection<StakeholderConsultation> consultations) {
    var savedConsultations = consultationRepository.saveAll(consultations);
    var consultationInfo = consultationAuditInfoFactory.build(requestId, savedConsultations);
    auditService.auditEvent(AuditEventSubject.CONSULTATION_CREATE, consultationInfo);
  }

  private void saveProjects(RequestUpdateParams params) {
    var updatedProjects = params.getUpdatedProjects();
    saveCreditingPeriods(params, updatedProjects);
    var savedProjects = IterableUtils.toList(projectRepository.saveAll(updatedProjects));
    acceptPiiMetadataUponPreliminaryReviewCompletion(params.getUpdatedRequest());
    var auditInfo = requestAuditInfoFactory.buildProjectStatusInfo(savedProjects);
    auditService.auditEvent(AuditEventSubject.PROJECT_UPDATE, auditInfo);
  }

  private void saveActivities(Collection<RequestActivity> activities) {
    requestActivityRepository.saveAll(activities);
  }

  private void saveRounds(Collection<RequestRound> rounds) {
    requestRoundRepository.saveAll(rounds);
  }

  private void saveCreditingPeriods(
      RequestUpdateParams params, Collection<Project> updatedProjects) {
    params
        .getOriginalProjects()
        .forEach(
            originalProject ->
                saveCreditingPeriods(originalProject, updatedProjects, params.getUpdatedRequest()));
  }

  private void saveCreditingPeriods(
      Project originalProject, Collection<Project> updatedProjects, Request request) {
    findById(originalProject.getId(), updatedProjects)
        .ifPresent(
            updatedProject ->
                creditingPeriodTransaction.apply(originalProject, updatedProject, request));
  }

  private void saveConversation(RequestUpdateParams params) {
    var conversation = params.getConversation();
    conversation.ifPresent(conversationTransaction::update);
  }

  private void acceptPiiMetadataUponPreliminaryReviewCompletion(Request request) {
    if (request.isApprovedPreliminaryReviewRequest()) {
      piiMetadataService.acceptProjectPii(request.getProject().getId());
    }
  }

  private static Optional<Project> findById(Integer id, Collection<Project> projects) {
    return projects.stream().filter(project -> Objects.equals(project.getId(), id)).findFirst();
  }
}
