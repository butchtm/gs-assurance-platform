package org.goldstandard.assurance.api.service.pub.stakeholder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.BatchProjectView;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.StakeholderProjectDetailView;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderBatchRequestView;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderRequestView;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderBatchProjectsViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderBatchRequestsViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderProjectsDetailViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderRequestsViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicStakeholderFinderDetailTest {

  private static final Integer REQUEST_ID = 100;
  private static final Integer PROJECT_ID = 200;
  private static final Integer BATCH_REQUEST_ID = 300;
  private static final Integer BATCH_ID = 400;
  private static final String BATCH_GSID = "GSBATCH";
  private static final Integer BATCH_PROJECT_ID = 600;
  public static final String GSID = "GS12345";
  private static final StakeholderProjectDetailView PROJECT_VIEW =
      StakeholderProjectDetailView.builder()
          .projectId(PROJECT_ID)
          .gsid(GSID)
          .description("description")
          .build();
  private static final StakeholderRequestView REQUEST_VIEW =
      StakeholderRequestView.builder()
          .requestId(REQUEST_ID)
          .requestName("requestName")
          .batchId(BATCH_ID)
          .build();
  private static final StakeholderBatchRequestView BATCH_REQUEST_VIEW =
      StakeholderBatchRequestView.builder()
          .batchId(BATCH_ID)
          .requestId(BATCH_REQUEST_ID)
          .requestName("requestName")
          .parentRequestId(REQUEST_ID)
          .build();
  private static final BatchProjectView BATCH_PROJECT_VIEW =
      BatchProjectView.builder().projectId(BATCH_PROJECT_ID).gsid(BATCH_GSID).build();

  @InjectMocks PublicStakeholderFinderDetail service;
  @Mock PublicStakeholderProjectsDetailViewRepository projectsViewRepository;
  @Mock PublicStakeholderRequestsViewRepository requestsViewRepository;
  @Mock PublicStakeholderBatchRequestsViewRepository batchRequestsViewRepository;
  @Mock PublicStakeholderBatchProjectsViewRepository batchProjectsViewRepository;
  @Mock PublicStakeholderAttributesService attributesService;

  @Test
  void getAllProjectsInStakeholderConsultation() {
    mockProjectView();
    mockRequestView();
    mockBatchProjectView();
    mockBatchRequestView();

    var project = service.getProjectDetailsInConsultation(GSID);

    var request = project.getRequests().getFirst();
    var batchedProject = request.getBatchedProjectChildren().getFirst();
    var batchedRequest = batchedProject.getRequests().getFirst();

    assertThat(project.getGsid()).isEqualTo(GSID);
    assertThat(project.getProjectDescription()).isEqualTo(PROJECT_VIEW.getDescription());
    assertThat(request.getRequestId()).isEqualTo(REQUEST_ID);
    assertThat(batchedProject.getGsid()).isEqualTo(BATCH_GSID);
    assertThat(batchedRequest.getRequestId()).isEqualTo(BATCH_REQUEST_ID);
  }

  private void mockProjectView() {
    when(projectsViewRepository.findById(GSID)).thenReturn(Optional.of(PROJECT_VIEW));
  }

  private void mockRequestView() {
    when(requestsViewRepository.findByGsid(PROJECT_VIEW.getGsid()))
        .thenReturn(List.of(REQUEST_VIEW));
  }

  private void mockBatchRequestView() {
    when(batchRequestsViewRepository.findAllByGsidAndParentRequestId(BATCH_GSID, REQUEST_ID))
        .thenReturn(List.of(BATCH_REQUEST_VIEW));
  }

  private void mockBatchProjectView() {
    when(batchProjectsViewRepository.findAllByBatchId(BATCH_ID))
        .thenReturn(List.of(BATCH_PROJECT_VIEW));
  }
}
