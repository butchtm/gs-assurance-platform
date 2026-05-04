package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.ConsultationType;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.StakeholderConsultation;
import org.goldstandard.assurance.api.entity.fixtures.ConsultationTypeMother;
import org.goldstandard.assurance.api.entity.view.BatchParentIdView;
import org.goldstandard.assurance.api.repository.BatchParentIdViewRepository;
import org.goldstandard.assurance.api.repository.StakeholderConsultationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StakeholderConsultationFinderTest {

  @InjectMocks StakeholderConsultationFinder consultationFinder;

  @Mock StakeholderConsultationRepository consultationRepository;

  @Mock BatchParentIdViewRepository batchParentRepository;

  @Mock Clock clock;

  @Test
  void shouldReturnInConsultationTrueIfConsultationsExist() {
    var request = givenRequest();
    var expectedConsultations = List.of(mock(StakeholderConsultation.class));
    var now = mockTimeNow();
    when(consultationRepository.getAllInConsultationByRequestId(request.getId(), now))
        .thenReturn(expectedConsultations);

    var inConsultation = consultationFinder.isInConsultation(request);

    assertThat(inConsultation).isTrue();
  }

  @Test
  void shouldReturnInConsultationTrueIfConsultationsExistOnBatchParentForChild() {
    var childRequest = givenRequest(5);
    var expectedConsultations = List.of(mock(StakeholderConsultation.class));
    var now = mockTimeNow();
    var parentRequest = givenBatchParentRequestForChild(childRequest);
    when(consultationRepository.getAllInConsultationByRequestId(parentRequest.getId(), now))
        .thenReturn(expectedConsultations);

    var inConsultation = consultationFinder.isInConsultation(childRequest);

    assertThat(inConsultation).isTrue();
  }

  @Test
  void shouldReturnInConsultationFalseIfConsultationsDoNotExist() {
    var request = givenRequest();
    List<StakeholderConsultation> expectedConsultations = List.of();
    var now = mockTimeNow();
    when(consultationRepository.getAllInConsultationByRequestId(request.getId(), now))
        .thenReturn(expectedConsultations);

    var inConsultation = consultationFinder.isInConsultation(request);

    assertThat(inConsultation).isFalse();
  }

  @Test
  void shouldReturnPrivateLiveStakeholderConsultationsForGivenRequestProject() {
    var project = givenProject(1);
    var request = givenRequest(project);
    var expectedConsultation = givenConsultation(project, ConsultationTypeMother.tacType());
    var consultations =
        List.of(
            givenConsultation(givenProject(8), ConsultationTypeMother.tacType()),
            expectedConsultation,
            givenConsultation(project, ConsultationTypeMother.publicType()));
    var now = mockTimeNow();
    when(consultationRepository.getAllInConsultationByRequestId(request.getId(), now))
        .thenReturn(consultations);

    var consultation = consultationFinder.getPrivateConsultation(request);

    assertThat(consultation).contains(expectedConsultation);
  }

  @Test
  void shouldReturnLiveStakeholderConsultationsForGivenRequestId() {
    var request = givenRequest();
    int requestId = request.getId();
    var expectedConsultations = List.of(mock(StakeholderConsultation.class));
    var now = mockTimeNow();
    when(consultationRepository.getAllInConsultationByRequestId(request.getId(), now))
        .thenReturn(expectedConsultations);

    var consultations = consultationFinder.getLiveStakeholderConsultations(requestId);

    assertThat(consultations).isEqualTo(expectedConsultations);
  }

  @Test
  void shouldReturnUnnotifiedLiveStakeholderConsultations() {
    var expectedConsultations = List.of(mock(StakeholderConsultation.class));
    var now = mockTimeNow();
    when(consultationRepository.getAllUnnotifiedInConsultation(now))
        .thenReturn(expectedConsultations);

    var consultations = consultationFinder.getLiveUnnotifiedStakeholderConsultations();

    assertThat(consultations).isEqualTo(expectedConsultations);
  }

  private ZonedDateTime mockTimeNow() {
    var now = ZonedDateTime.now();
    when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    when(clock.instant()).thenReturn(now.toInstant());
    return now;
  }

  private Request givenBatchParentRequestForChild(Request childRequest) {
    var parentRequest = givenRequest(childRequest.getId() + 1);
    var batchParentView = mock(BatchParentIdView.class);
    when(batchParentView.getParentRequestId()).thenReturn(parentRequest.getId());
    when(batchParentRepository.getBatchParentIdView(childRequest.getId()))
        .thenReturn(Optional.of(batchParentView));
    return parentRequest;
  }

  private static Project givenProject(int id) {
    var project = new Project();
    project.setId(id);
    return project;
  }

  private static Request givenRequest() {
    return givenRequest(new Project());
  }

  private static Request givenRequest(int id) {
    return givenRequest(id, new Project());
  }

  private static Request givenRequest(Project project) {
    return givenRequest(5, project);
  }

  private static Request givenRequest(int id, Project project) {
    var request = new Request();
    request.setId(id);
    request.setProject(project);
    return request;
  }

  private static StakeholderConsultation givenConsultation(Project project, ConsultationType type) {
    var consultation = new StakeholderConsultation();
    consultation.setProject(project);
    consultation.setConsultationType(type);
    return consultation;
  }
}
