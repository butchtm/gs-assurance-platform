package org.goldstandard.assurance.api.service.pub.stakeholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectDetailDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.request.PublicStakeholderRequestDetailDTO;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBase;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderBatchRequestView;
import org.goldstandard.assurance.api.entity.view.stakeholder.request.StakeholderRequestView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderBatchProjectsViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderBatchRequestsViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderProjectsDetailViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderRequestsViewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicStakeholderFinderDetail {

  private final PublicStakeholderProjectsDetailViewRepository projectsViewRepository;
  private final PublicStakeholderRequestsViewRepository requestsViewRepository;
  private final PublicStakeholderBatchRequestsViewRepository batchRequestsViewRepository;
  private final PublicStakeholderBatchProjectsViewRepository batchProjectsViewRepository;
  private final PublicStakeholderAttributesService attributes;

  public PublicStakeholderProjectDetailDTO getProjectDetailsInConsultation(String gsid)
      throws NotFoundException {
    return projectsViewRepository
        .findById(gsid)
        .map(project -> mapToProjectDetailDto(project, null))
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Public project [%s] not available to view under stakeholder consultation",
                    gsid));
  }

  private PublicStakeholderProjectDetailDTO mapToProjectDetailDto(
      ProjectBase project, Integer parentRequestId) {
    var projectDto = new PublicStakeholderProjectDetailDTO(project);
    attributes.setProjectCreditingPeriods(projectDto, project.getProjectId());
    if (parentRequestId == null) {
      projectDto.setRequests(getAllRequestsInConsultation(projectDto.getGsid()));
    } else {
      projectDto.setRequests(getAllBatchChildRequests(projectDto.getGsid(), parentRequestId));
    }
    return projectDto;
  }

  private List<PublicStakeholderRequestDetailDTO> getAllRequestsInConsultation(String projectGsid) {
    var requestViews = requestsViewRepository.findByGsid(projectGsid);
    List<PublicStakeholderRequestDetailDTO> requests = new ArrayList<>();
    for (var requestView : requestViews) {
      requests.add(createDetailedRequestDto(projectGsid, requestView));
    }
    return requests;
  }

  private List<PublicStakeholderRequestDetailDTO> getAllBatchChildRequests(
      String gsid, Integer parentRequestId) {
    var requestViews =
        batchRequestsViewRepository.findAllByGsidAndParentRequestId(gsid, parentRequestId);
    List<PublicStakeholderRequestDetailDTO> requests = new ArrayList<>();
    for (var requestView : requestViews) {
      requests.add(createBatchChildRequestDto(requestView));
    }
    return requests;
  }

  private PublicStakeholderRequestDetailDTO createDetailedRequestDto(
      String projectGsid, StakeholderRequestView requestView) {
    return PublicStakeholderRequestDetailDTO.builder()
        .requestId(requestView.getRequestId())
        .requestName(requestView.getRequestName())
        .requestStatus(requestView.getRequestStatus())
        .requestCreditingPeriodId(requestView.getRequestCreditingPeriod())
        .requestCreditingPeriod(
            attributes.getRequestCreditingPeriod(
                requestView.getRequestId(), requestView.getRequestCreditingPeriod(), projectGsid))
        .emissionReductionAmount(requestView.getEmissionReductionAmount())
        .consultationStartDate(requestView.getConsultationStartDate())
        .consultationEndDate(requestView.getConsultationEndDate())
        .documents(attributes.getRequestDocuments(requestView.getRequestId()))
        .comments(attributes.getRequestComments(requestView.getRequestId()))
        .batchedProjectChildren(
            getBatchChildProjects(
                requestView.getBatchId(), projectGsid, requestView.getRequestId()))
        .requestMethodologies(attributes.getRequestMethodologies(requestView.getRequestId()))
        .build();
  }

  private List<PublicStakeholderProjectDetailDTO> getBatchChildProjects(
      Integer batchId, String parentGsid, Integer requestId) {
    return batchProjectsViewRepository.findAllByBatchId(batchId).stream()
        .map(batchChild -> mapToProjectDetailDto(batchChild, requestId))
        .filter(project -> !Objects.equals(project.getGsid(), parentGsid))
        .toList();
  }

  private PublicStakeholderRequestDetailDTO createBatchChildRequestDto(
      StakeholderBatchRequestView requestView) {
    return PublicStakeholderRequestDetailDTO.builder()
        .requestId(requestView.getRequestId())
        .requestName(requestView.getRequestName())
        .requestStatus(requestView.getRequestStatus())
        .requestCreditingPeriodId(requestView.getRequestCreditingPeriod())
        .requestCreditingPeriod(
            attributes.getRequestCreditingPeriod(
                requestView.getRequestId(),
                requestView.getRequestCreditingPeriod(),
                requestView.getGsid()))
        .emissionReductionAmount(requestView.getEmissionReductionAmount())
        .build();
  }
}
