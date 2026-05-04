package org.goldstandard.assurance.api.service.pub.stakeholder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.MethodologyConverter;
import org.goldstandard.assurance.api.converter.ProjectCreditingPeriodConverter;
import org.goldstandard.assurance.api.dto.CreditingPeriodDTO;
import org.goldstandard.assurance.api.dto.RequestMethodologyDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.CommentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.attributes.DocumentDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectDetailDTO;
import org.goldstandard.assurance.api.repository.ProjectCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestCreditingPeriodRepository;
import org.goldstandard.assurance.api.repository.RequestMethodologyRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderCommentViewRepository;
import org.goldstandard.assurance.api.repository.pub.stakeholder.PublicStakeholderDocumentsViewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicStakeholderAttributesService {
  public static final String ANONYMOUS_USERNAME = "anonymous";
  private final PublicStakeholderDocumentsViewRepository documentsViewRepository;
  private final PublicStakeholderCommentViewRepository commentViewRepository;
  private final RequestMethodologyRepository methodologyRepository;

  private final ProjectCreditingPeriodRepository projectCreditingPeriodRepository;
  private final ProjectCreditingPeriodConverter creditingPeriodConverter;
  private final RequestCreditingPeriodRepository requestCreditingPeriodRepository;
  private final MethodologyConverter methodologyConverter;

  public PublicStakeholderProjectDetailDTO setProjectCreditingPeriods(
      PublicStakeholderProjectDetailDTO project, Integer projectId) {
    project.setProjectCreditingPeriods(getCreditingPeriods(projectId));
    return project;
  }

  public List<CommentDTO> getRequestComments(Integer requestId) {
    return commentViewRepository.findByRequestId(requestId).stream()
        .map(CommentDTO::new)
        .map(PublicStakeholderAttributesService::checkForAnonymousFilter)
        .toList();
  }

  public List<DocumentDTO> getRequestDocuments(Integer requestId) {
    return documentsViewRepository.findByRequestId(requestId).stream()
        .map(DocumentDTO::new)
        .toList();
  }

  public Collection<RequestMethodologyDTO> getRequestMethodologies(Integer requestId) {
    var methodologies = methodologyRepository.getAllByRequestId(requestId);
    return methodologyConverter.toRequestMethodologyDtos(methodologies);
  }

  public CreditingPeriodDTO getRequestCreditingPeriod(
      Integer requestId, Integer periodId, String gsid) {
    if (Objects.nonNull(periodId)) {
      return getExistingRequestCreditingPeriod(periodId, gsid);
    }
    return requestCreditingPeriodRepository
        .findById(requestId)
        .map(creditingPeriodConverter::fromRequestCreditingPeriod)
        .orElse(null);
  }

  private CreditingPeriodDTO getExistingRequestCreditingPeriod(Integer periodId, String gsid) {
    return projectCreditingPeriodRepository
        .findByProjectGsidAndPeriodId(gsid, periodId)
        .map(creditingPeriodConverter::toDto)
        .orElse(null);
  }

  private List<CreditingPeriodDTO> getCreditingPeriods(Integer projectId) {
    var creditingPeriods = projectCreditingPeriodRepository.findAllByProjectId(projectId);
    return creditingPeriodConverter.toDtos(creditingPeriods);
  }

  private static CommentDTO checkForAnonymousFilter(CommentDTO comment) {
    if (comment.isAnonymous()) {
      comment.setUploadedByName(ANONYMOUS_USERNAME);
    }
    return comment;
  }
}
