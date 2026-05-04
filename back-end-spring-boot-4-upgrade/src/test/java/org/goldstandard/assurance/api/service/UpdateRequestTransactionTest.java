package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goldstandard.assurance.api.entity.lookup.RequestType.GENERIC;
import static org.goldstandard.assurance.api.service.AuditEventSubject.CONSULTATION_CREATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.PROJECT_UPDATE;
import static org.goldstandard.assurance.api.service.AuditEventSubject.REQUEST_UPDATE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.goldstandard.assurance.api.entity.Conversation;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.RequestMethodology;
import org.goldstandard.assurance.api.entity.RequestRound;
import org.goldstandard.assurance.api.entity.RequestUpdateParams;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.activity.RequestActivity;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestCreditingPeriod;
import org.goldstandard.assurance.api.entity.creditingperiod.RequestMonitoringPeriod;
import org.goldstandard.assurance.api.entity.lookup.RequestType;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateRequestTransactionTest {

  @InjectMocks UpdateRequestTransaction transaction;

  @Mock RequestRepository requestRepository;

  @Mock StakeholderConsultationRepository consultationRepository;

  @Mock RequestAuditInfoFactory requestAuditInfoFactory;

  @Mock ConsultationAuditInfoFactory consultationAuditInfoFactory;

  @Mock ProjectRepository projectRepository;

  @Mock AuditService auditService;

  @Mock RequestMethodologyRepository methodologyRepository;

  @Mock RequestActivityRepository activityRepository;

  @Mock RequestRoundRepository requestRoundRepository;

  @Mock CreditingPeriodTransaction creditingPeriodTransaction;

  @Mock RequestCreditingPeriodRepository requestCreditingPeriodRepository;

  @Mock RequestMonitoringPeriodRepository requestMonitoringPeriodRepository;

  @Mock PiiMetadataService piiMetadataService;

  @Mock ConversationTransaction conversationTransaction;

  @Captor ArgumentCaptor<Request> requestCaptor;

  @Captor ArgumentCaptor<Collection<Request>> childRequestsCaptor;

  @Captor ArgumentCaptor<Collection<StakeholderConsultation>> consultationsCaptor;

  @Captor ArgumentCaptor<Map<String, Object>> mapCaptor;

  @Captor ArgumentCaptor<Collection<Project>> projectsCaptor;

  @Captor ArgumentCaptor<Project> projectCaptor;

  @Captor ArgumentCaptor<Collection<RequestActivity>> activitiesCaptor;

  @Captor ArgumentCaptor<Collection<RequestRound>> requestRoundCaptor;

  @Captor ArgumentCaptor<Collection<RequestMethodology>> methodologiesCaptor;

  @Captor ArgumentCaptor<Conversation> conversationCaptor;

  @Test
  void shouldUpdateRequest() {
    var request = buildRequestWithId(1);
    givenSavedRequest(request);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(requestRepository).save(requestCaptor.capture());
    assertThat(requestCaptor.getValue()).isEqualTo(request);
  }

  @Test
  void shouldDeleteRequestCreditingPeriodIfUpdatedRequestDoesNotHaveDraftCreditingPeriod() {
    var request = buildRequestWithId(1);
    givenSavedRequest(request);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(requestCreditingPeriodRepository).deleteById(request.getId());
  }

  @Test
  void shouldNotDeleteRequestCreditingPeriodIfUpdatedRequestHasDraftCreditingPeriod() {
    var request =
        Request.builder()
            .id(1)
            .requestType(new RequestType(GENERIC))
            .draftCreditingPeriod(mock(RequestCreditingPeriod.class))
            .build();
    givenSavedRequest(request);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(requestCreditingPeriodRepository, never()).deleteById(anyInt());
  }

  @Test
  void shouldDeleteRequestMonitoringPeriodIfUpdatedRequestDoesNotHaveDraftMonitoringPeriod() {
    var request = buildRequestWithId(1);
    givenSavedRequest(request);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(requestMonitoringPeriodRepository).deleteById(request.getId());
  }

  @Test
  void shouldNotDeleteRequestMonitoringPeriodIfUpdatedRequestHasDraftMonitoringPeriod() {
    var request =
        Request.builder()
            .id(1)
            .requestType(new RequestType(GENERIC))
            .draftMonitoringPeriod(mock(RequestMonitoringPeriod.class))
            .build();
    givenSavedRequest(request);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(requestMonitoringPeriodRepository, never()).deleteById(anyInt());
  }

  @Test
  void shouldSaveChildRequests() {
    var request = buildRequestWithId(1);
    givenSavedRequest(request);
    var childRequest = buildRequestWithId(2);
    givenSavedRequest(request);
    var updateParams =
        buildRequestUpdateParams(request).withUpdatedChildRequests(List.of(childRequest));

    transaction.updateRequest(updateParams);

    verify(requestRepository).saveAll(childRequestsCaptor.capture());
    assertThat(childRequestsCaptor.getValue()).containsExactly(childRequest);
  }

  @Test
  void shouldDeleteOriginalMethodologies() {
    var originalParentMethodology = mock(RequestMethodology.class);
    var originalParentRequest = givenRequestWithMethodology(originalParentMethodology);
    var originalChildMethodology = mock(RequestMethodology.class);
    var originalChildRequest = givenRequestWithMethodology(originalChildMethodology);

    var updatedMethodology = mock(RequestMethodology.class);
    var updatedRequest =
        buildRequestWithId(1).withRequestMethodologies(List.of(updatedMethodology));
    givenSavedRequest(updatedRequest);

    var updateParams =
        buildRequestUpdateParams(updatedRequest)
            .withOriginalRequest(originalParentRequest)
            .withOriginalChildRequests(List.of(originalChildRequest));

    transaction.updateRequest(updateParams);

    verify(methodologyRepository).deleteAll(methodologiesCaptor.capture());
    assertThat(methodologiesCaptor.getValue()).containsExactly(originalParentMethodology);
  }

  @Test
  void shouldSaveUpdatedMethodologies() {
    var updatedParentMethodology = mock(RequestMethodology.class);
    var updatedRequest =
        buildRequestWithId(1).withRequestMethodologies(List.of(updatedParentMethodology));
    givenSavedRequest(updatedRequest);
    var updatedChildMethodology = mock(RequestMethodology.class);
    var updatedChildRequest = givenRequestWithMethodology(updatedChildMethodology);

    var updateParams =
        buildRequestUpdateParams(updatedRequest)
            .withUpdatedChildRequests(List.of(updatedChildRequest));

    transaction.updateRequest(updateParams);

    verify(methodologyRepository).saveAll(methodologiesCaptor.capture());
    assertThat(methodologiesCaptor.getValue()).containsExactly(updatedParentMethodology);
  }

  @Test
  void shouldSaveAllConsultations() {
    var request = buildRequestWithId(2);
    givenSavedRequest(request);
    var consultations = List.of(new StakeholderConsultation());
    var savedConsultations = List.of(mock(StakeholderConsultation.class));
    when(consultationRepository.saveAll(consultations)).thenReturn(savedConsultations);
    var updateParams = buildRequestUpdateParams(request).withConsultations(consultations);

    transaction.updateRequest(updateParams);

    verify(consultationRepository).saveAll(consultationsCaptor.capture());
    assertThat(consultationsCaptor.getValue()).isEqualTo(consultations);
  }

  @Test
  void shouldNotSaveConsultationsIfEmpty() {
    var request = buildRequestWithId(3);
    givenSavedRequest(request);
    List<StakeholderConsultation> consultations = List.of();
    var updateParams = buildRequestUpdateParams(request).withConsultations(consultations);

    transaction.updateRequest(updateParams);

    verifyNoInteractions(consultationRepository);
  }

  @Test
  void shouldAuditRequestUpdate() {
    var requestId = 4;
    var request = buildRequestWithId(requestId);
    var project = buildProject();
    var savedRequest = givenSavedRequestWithProject(request, project);
    var expectedAuditInfo = new HashMap<String, Object>();
    when(requestAuditInfoFactory.buildRequestInfo(savedRequest, List.of()))
        .thenReturn(expectedAuditInfo);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(auditService).auditEvent(eq(REQUEST_UPDATE), mapCaptor.capture());
    assertThat(mapCaptor.getValue()).isEqualTo(expectedAuditInfo);
  }

  @Test
  void shouldAuditConsultationUpdatesIfPresent() {
    var requestId = 5;
    var request = buildRequestWithId(requestId);
    givenSavedRequest(request);
    var consultations = List.of(new StakeholderConsultation());
    var savedConsultations = List.of(mock(StakeholderConsultation.class));
    when(consultationRepository.saveAll(consultations)).thenReturn(savedConsultations);
    Map<String, Object> expectedAuditInfo = givenConsultationAuditInfo(request, savedConsultations);
    var updateParams = buildRequestUpdateParams(request).withConsultations(consultations);

    transaction.updateRequest(updateParams);

    verify(auditService).auditEvent(eq(CONSULTATION_CREATE), mapCaptor.capture());
    assertThat(mapCaptor.getValue()).isEqualTo(expectedAuditInfo);
  }

  @Test
  void shouldNotAuditConsultationCreationIfNotPresent() {
    var requestId = 6;
    var request = buildRequestWithId(requestId);
    givenSavedRequest(request);
    List<StakeholderConsultation> consultations = List.of();
    var updateParams = buildRequestUpdateParams(request).withConsultations(consultations);

    transaction.updateRequest(updateParams);

    verify(auditService, never()).auditEvent(eq(CONSULTATION_CREATE), anyMap());
  }

  @Test
  void shouldSaveProjectsIfProvided() {
    var project = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, project);
    var projects = List.of(new Project());
    var updateParams = buildRequestUpdateParams(request).withUpdatedProjects(projects);

    transaction.updateRequest(updateParams);

    verify(projectRepository).saveAll(projectsCaptor.capture());
    var savedProjects = projectsCaptor.getValue();
    assertThat(savedProjects).isEqualTo(projects);
  }

  @Test
  void shouldAuditProjectUpdatesIfProvided() {
    var project = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, project);
    var expectedAuditInfo = new HashMap<String, Object>();
    var updatedProjects = List.of(new Project());
    var expectedSavedProjects = List.of(project);
    when(projectRepository.saveAll(updatedProjects)).thenReturn(expectedSavedProjects);
    when(requestAuditInfoFactory.buildProjectStatusInfo(expectedSavedProjects))
        .thenReturn(expectedAuditInfo);
    var updateParams = buildRequestUpdateParams(request).withUpdatedProjects(updatedProjects);

    transaction.updateRequest(updateParams);

    verify(auditService).auditEvent(eq(PROJECT_UPDATE), mapCaptor.capture());
    assertThat(mapCaptor.getValue()).isEqualTo(expectedAuditInfo);
  }

  @Test
  void shouldSaveProjectCreditingPeriods() {
    var originalProject = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, originalProject);
    var updatedProject = buildProject();
    var updateParams =
        buildRequestUpdateParams(request)
            .withOriginalProjects(List.of(originalProject))
            .withUpdatedProjects(List.of(updatedProject));

    transaction.updateRequest(updateParams);

    verify(creditingPeriodTransaction)
        .apply(projectCaptor.capture(), projectCaptor.capture(), eq(request));
    assertThat(projectCaptor.getAllValues()).containsExactly(originalProject, updatedProject);
  }

  @Test
  void shouldSaveRequestActivities() {
    var project = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, project);
    var activities = List.of(mock(RequestActivity.class));
    var updateParams = buildRequestUpdateParams(request).withActivities(activities);

    transaction.updateRequest(updateParams);

    verify(activityRepository).saveAll(activitiesCaptor.capture());
    assertThat(activitiesCaptor.getValue()).isEqualTo(activities);
  }

  @Test
  void shouldSaveRequestRounds() {
    var project = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, project);
    var rounds = List.of(mock(RequestRound.class));
    var updateParams = buildRequestUpdateParams(request).withRounds(rounds);

    transaction.updateRequest(updateParams);

    verify(requestRoundRepository).saveAll(requestRoundCaptor.capture());
    assertThat(requestRoundCaptor.getValue()).isEqualTo(rounds);
  }

  @Test
  void shouldAcceptPiiMetadataWhenPreliminaryReviewIsApproved() {
    var project1 = buildProjectWithId(1);
    var updatedProjects = List.of(project1);
    var request = mock(Request.class);
    when(request.getId()).thenReturn(1);
    when(request.isApprovedPreliminaryReviewRequest()).thenReturn(true);
    when(request.getProject()).thenReturn(project1);
    var updateParams = buildRequestUpdateParams(request).withUpdatedProjects(updatedProjects);
    givenSavedProjects(updatedProjects);

    transaction.updateRequest(updateParams);

    verify(piiMetadataService).acceptProjectPii(project1.getId());
  }

  @Test
  void shouldNotAcceptPiiMetadataWhenPreliminaryReviewIsNotApproved() {
    var request = mock(Request.class);
    when(request.getId()).thenReturn(1);
    when(request.isApprovedPreliminaryReviewRequest()).thenReturn(false);
    var updateParams = buildRequestUpdateParams(request);

    transaction.updateRequest(updateParams);

    verify(piiMetadataService, never()).acceptProjectPii(anyInt());
  }

  @Test
  void shouldSaveConversationIfProvided() {
    var project = buildProject();
    var request = buildRequestWithId(1);
    givenSavedRequestWithProject(request, project);
    var conversation = new Conversation();
    var updateParams = buildRequestUpdateParams(request).withConversation(conversation);

    transaction.updateRequest(updateParams);

    verify(conversationTransaction).update(conversationCaptor.capture());
    assertThat(conversationCaptor.getValue()).isEqualTo(conversation);
  }

  private void givenSavedRequest(Request request) {
    var project = buildProject();
    givenSavedRequestWithProject(request, project);
  }

  private Project buildProject() {
    return Project.builder().id(999).build();
  }

  private Request givenSavedRequestWithProject(Request request, Project project) {
    var savedRequest = Request.builder().id(request.getId()).project(project).build();
    when(requestRepository.save(request)).thenReturn(savedRequest);
    return savedRequest;
  }

  private Map<String, Object> givenConsultationAuditInfo(
      Request request, Collection<StakeholderConsultation> consultations) {
    Map<String, Object> info = Map.of("key", "value");
    when(consultationAuditInfoFactory.build(request.getId(), consultations)).thenReturn(info);
    return info;
  }

  private static Request buildRequestWithId(int id) {
    return Request.builder().id(id).requestType(new RequestType(GENERIC)).build();
  }

  private static Request givenRequestWithMethodology(RequestMethodology methodology) {
    return buildRequestWithId(1).withRequestMethodologies(List.of(methodology));
  }

  private static RequestUpdateParams buildRequestUpdateParams(Request request) {
    return RequestUpdateParams.builder()
        .updatedRequest(request)
        .updatedChildRequests(Collections.emptyList())
        .consultations(Collections.emptyList())
        .originalProjects(Collections.emptyList())
        .updatedProjects(Collections.emptyList())
        .rounds(Collections.emptyList())
        .activities(Collections.emptyList())
        .conversation(null)
        .build();
  }

  private Project buildProjectWithId(int id) {
    var project = mock(Project.class);
    when(project.getId()).thenReturn(id);
    return project;
  }

  private void givenSavedProjects(Collection<Project> projects) {
    when(projectRepository.saveAll(projects)).thenReturn(projects);
  }
}
