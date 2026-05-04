package org.goldstandard.assurance.api.service.pub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.pub.PublicDataMapper;
import org.goldstandard.assurance.api.dto.pub.PublicProject;
import org.goldstandard.assurance.api.dto.pub.PublicRequest;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.repository.pub.PublicDocumentViewRepository;
import org.goldstandard.assurance.api.repository.pub.PublicRequestsViewRepository;
import org.goldstandard.assurance.api.service.CommentFinder;
import org.goldstandard.assurance.api.service.ProjectFinder;
import org.goldstandard.assurance.api.service.RequestFinder;
import org.springframework.stereotype.Service;

/** This service deals with any public facing data. */
@Service
@RequiredArgsConstructor
public class PublicService {

  private final ProjectFinder finder;
  private final RequestFinder requestFinder;
  private final PublicRequestsViewRepository publicRequestsRepository;
  private final PublicDocumentViewRepository publicDocumentRepository;
  private final CommentFinder commentFinder;
  private final PublicRequestVisibilityEvaluator visibilityEvaluator;
  private final PublicDataMapper publicMapper;
  private final RequestRepository requestRepository;

  public PublicProject getPublicProjectRequestDocumentsForGSID(String gsid) {
    var project = finder.forceGetProjectByGsId(gsid);
    return PublicProject.builder()
        .gsid(project.getGsid())
        .name(project.getName())
        .requests(toRequests(gsid))
        .build();
  }

  public String getRequestProjectGsId(Integer requestId) {
    var request = requestRepository.findRequestProjectById(requestId);
    return request.getProjectGsid();
  }

  private Collection<PublicRequest> toRequests(String gsid) {
    var publicRequests = new ArrayList<PublicRequest>();
    var requests = publicRequestsRepository.findByGsidOrderByRequestIndexAscRequestIdAsc(gsid);
    for (var request : requests) {
      var lookupRequestId = requestFinder.getParentRequestIdOrRequestId(request.getRequestId());
      var documents = publicDocumentRepository.findByGsidAndRequestId(gsid, lookupRequestId);
      var comments = commentFinder.getCommentsByRequestId(lookupRequestId);
      if (visibilityEvaluator.shouldShowRequestOnPublicPage(request, documents, comments)) {
        publicRequests.add(publicMapper.toPublicRequest(request, documents, comments));
      }
    }
    return Collections.unmodifiableCollection(publicRequests);
  }
}
