package org.goldstandard.assurance.api.service.pub.stakeholder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.CreditingPeriodMethodologyDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBasicView;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderRequestView;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderProjectsBasicViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderRequestsViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PublicStakeholderFinderBasicTest {

  private static final Integer REQUEST_ID = 100;
  private static final Integer PROJECT_ID = 200;
  public static final int CREDITING_PERIOD_ID = 21;

  public static final String GSID = "GS12345";
  private static final ProjectBasicView PROJECT_VIEW =
      ProjectBasicView.builder()
          .projectId(PROJECT_ID)
          .gsid(GSID)
          .projectName("test project")
          .build();
  private static final StakeholderRequestView REQUEST_VIEW =
      StakeholderRequestView.builder()
          .requestId(REQUEST_ID)
          .requestName("requestName")
          .requestStatus("in Assignment")
          .requestCreditingPeriod(CREDITING_PERIOD_ID)
          .build();

  @InjectMocks PublicStakeholderFinderBasic service;
  @Mock PublicStakeholderProjectsBasicViewRepository projectsViewRepository;
  @Mock PublicStakeholderRequestsViewRepository requestsViewRepository;
  @Mock PublicStakeholderAttributesService attributes;

  @Test
  void getAllProjectsInStakeholderConsultation() {
    mockProjectView();
    mockRequestView();
    mockCreditingPeriod();

    var result = service.getAllBasicProjectsInConsultation(Pageable.ofSize(10));

    var project = result.getContent().getFirst();
    var request = result.getContent().getFirst().getRequests().getFirst();
    assertThat(project.getGsid()).isEqualTo(GSID);
    assertThat(project.getName()).isEqualTo(PROJECT_VIEW.getProjectName());
    assertThat(request.getRequestId()).isEqualTo(REQUEST_ID);
    assertThat(request.getRequestStatus()).isEqualTo(REQUEST_VIEW.getRequestStatus());
  }

  @Test
  void stakeholderRequestsHaveRequestCreditingPeriodWithMethodologies() {
    mockProjectView();
    mockRequestView();
    var expectedMethodology = CreditingPeriodMethodologyDTO.builder().id(1).build();
    mockCreditingPeriod(List.of(expectedMethodology));

    var result = service.getAllBasicProjectsInConsultation(Pageable.ofSize(10));

    var request = result.getContent().getFirst().getRequests().getFirst();
    assertThat(request.getRequestCreditingPeriod().getMethodologies())
        .isEqualTo(List.of(expectedMethodology));
  }

  @Test
  void stakeholderRequestsHaveMethodologiesIfPresentOnRequest() {
    mockProjectView();
    mockRequestView();
    mockCreditingPeriod();
    var expectedRequestMethodology = mock(RequestMethodologyDTO.class);
    when(attributes.getRequestMethodologies(REQUEST_ID))
        .thenReturn(List.of(expectedRequestMethodology));

    var result = service.getAllBasicProjectsInConsultation(Pageable.ofSize(10));

    var request = result.getContent().getFirst().getRequests().getFirst();
    assertThat(request.getRequestMethodologies()).containsExactly(expectedRequestMethodology);
  }

  private void mockProjectView() {
    Page<ProjectBasicView> projectPage = new PageImpl<>(List.of(PROJECT_VIEW));
    when(projectsViewRepository.findAll(any(Pageable.class))).thenReturn(projectPage);
  }

  private void mockRequestView() {
    when(requestsViewRepository.findByGsid(PROJECT_VIEW.getGsid()))
        .thenReturn(List.of(REQUEST_VIEW));
  }

  private void mockCreditingPeriod() {
    mockCreditingPeriod(Collections.emptyList());
  }

  private void mockCreditingPeriod(Collection<CreditingPeriodMethodologyDTO> methodologies) {
    var periodDto = CreditingPeriodDTO.builder().methodologies(methodologies).build();
    when(attributes.getRequestCreditingPeriod(
            REQUEST_VIEW.getRequestId(),
            REQUEST_VIEW.getRequestCreditingPeriod(),
            PROJECT_VIEW.getGsid()))
        .thenReturn(periodDto);
  }
}
