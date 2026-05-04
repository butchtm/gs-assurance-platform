package org.goldstandard.assurance.api.service.pub.stakeholder;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectBasicDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.request.PublicStakeholderRequestBasicDTO;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBasicView;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderRequestView;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderProjectsBasicViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderRequestsViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicStakeholderFinderBasic {

  private final PublicStakeholderProjectsBasicViewRepository projectsViewRepository;
  private final PublicStakeholderRequestsViewRepository requestsViewRepository;
  private final PublicStakeholderAttributesService attributes;

  public Page<PublicStakeholderProjectBasicDTO> getAllBasicProjectsInConsultation(
      Pageable pageable) {
    return projectsViewRepository.findAll(pageable).map(this::mapBasicProjectToDto);
  }

  private PublicStakeholderProjectBasicDTO mapBasicProjectToDto(ProjectBasicView project) {
    var projectDto = new PublicStakeholderProjectBasicDTO(project);
    projectDto.setRequests(getAllRequestsInConsultation(projectDto.getGsid()));
    return projectDto;
  }

  private List<PublicStakeholderRequestBasicDTO> getAllRequestsInConsultation(String projectGsid) {
    var requestViews = requestsViewRepository.findByGsid(projectGsid);
    List<PublicStakeholderRequestBasicDTO> requests = new ArrayList<>();
    for (var requestView : requestViews) {
      requests.add(createBasicRequestDto(requestView, projectGsid));
    }
    return requests;
  }

  private PublicStakeholderRequestBasicDTO createBasicRequestDto(
      StakeholderRequestView requestView, String projectGsid) {
    return PublicStakeholderRequestBasicDTO.builder()
        .requestId(requestView.getRequestId())
        .requestName(requestView.getRequestName())
        .requestStatus(requestView.getRequestStatus())
        .requestMethodologies(attributes.getRequestMethodologies(requestView.getRequestId()))
        .requestCreditingPeriod(
            attributes.getRequestCreditingPeriod(
                requestView.getRequestId(), requestView.getRequestCreditingPeriod(), projectGsid))
        .consultationStartDate(requestView.getConsultationStartDate())
        .consultationEndDate(requestView.getConsultationEndDate())
        .batchId(requestView.getBatchId())
        .build();
  }
}
