package org.goldstandard.assurance.api.facade;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.RequestConverter;
import org.goldstandard.assurance.api.converter.TacRequestConverter;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.dto.TacRequestDTO;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.service.ProjectAccessPolicy;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestAccessPolicy;
import org.goldstandard.assurance.api.service.RequestCreator;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.goldstandard.assurance.api.service.RequestUpdater;
import org.goldstandard.assurance.api.service.conversation.UnreadMessageFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RequestFacade {
  private final RequestCreator creator;
  private final RequestUpdater updater;
  private final ProjectFinder projectFinder;
  private final RequestFinder requestFinder;
  private final RequestConverter mapper;
  private final TacRequestConverter tacMapper;
  private final RequestAccessPolicy requestAccessPolicy;
  private final ProjectAccessPolicy projectAccessPolicy;
  private final UnreadMessageFinder unreadMessageFinder;

  public Request createAndSaveRequest(Integer projectId, RequestCreateUpdateDTO create) {
    var project = projectFinder.forceGetProjectById(projectId);
    requestAccessPolicy.validateCanCreateRequest(project);
    return creator.create(projectId, create);
  }

  public void updateRequest(Integer projectId, RequestCreateUpdateDTO update) {
    var request = requestFinder.getRequestById(update.getId());
    updater.update(request, update, projectId);
  }

  @Transactional(readOnly = true)
  public Page<TacRequestDTO> getRequestsInConsultationByCompetencies(Pageable pageable) {
    return requestFinder
        .getRequestsInConsultationByCompetency(pageable)
        .map(tacMapper::toTacRequestDto);
  }

  @Transactional(readOnly = true)
  public Page<TacRequestDTO> getRequestsInConsultation(Pageable pageable) {
    return requestFinder.getRequestsInConsultation(pageable).map(tacMapper::toTacRequestDto);
  }

  @Transactional(readOnly = true)
  public Page<RequestDTO> getRequestDetailPage(Integer projectId, Pageable pageable) {
    var page = getRequestPage(projectId, pageable);
    var requestIds = page.stream().map(Request::getId).toList();
    var unreadMessageCounts =
        unreadMessageFinder.getUnreadMessageCountForAuthenticatedUserAndRequests(requestIds);
    return getRequestPage(projectId, pageable)
        .map(request -> mapper.toRequestDetailDTO(request, unreadMessageCounts));
  }

  @Transactional(readOnly = true)
  public RequestDTO getRequestDetailById(Integer requestId) {
    var request = requestFinder.getRequestById(requestId);
    projectAccessPolicy.validateUserCanViewProject(request.getProject());
    var unreadMessages =
        unreadMessageFinder.getUnreadMessageCountForAuthenticatedUserAndRequest(requestId);
    return mapper.toRequestDetailDTO(request, unreadMessages);
  }

  private Page<Request> getRequestPage(Integer projectId, Pageable pageable) {
    if (Objects.isNull(projectId)) {
      return requestFinder.getAllRequests(pageable);
    }
    return requestFinder.getByProjectId(projectId, pageable);
  }
}
