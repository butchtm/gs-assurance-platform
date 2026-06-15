package org.goldstandard.assurance.api.service;

import static org.goldstandard.assurance.api.service.ToPageConverter.toPage;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.view.BatchParentIdView;
import org.goldstandard.assurance.api.exception.DataException;
import org.goldstandard.assurance.api.exception.DocumentNotAssociatedWithRequestException;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.BatchParentIdViewRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.account.competencies.AccountCompetenciesFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestFinder {
  private final RequestRepository requestRepository;

  private final ProjectFinder projectFinder;
  private final PermissionsService permissionsService;
  private final AssignmentFinder assignmentFinder;
  private final AccountCompetenciesFilter competenciesFilter;
  private final BatchParentIdViewRepository batchParentRepository;
  private final RequestSpecFactory specFactory;

  public Page<Request> getRequestsInConsultation(Pageable pageable) {
    return requestRepository.findAllInConsultation(pageable);
  }

  public Page<Request> getRequestsInConsultationByCompetency(Pageable pageable) {
    var requests = getAllSortedRequestsInConsultationFilteredByCompetency(pageable);
    return toPage(pageable, requests);
  }

  public Request getRequestByInvoiceId(Integer invoiceId) {
    return requestRepository
        .findByInvoiceId(invoiceId)
        .orElseThrow(() -> new DataException("Request not found for invoice id %d", invoiceId));
  }

  public Page<Request> getAllRequests(Pageable pageable) {
    if (permissionsService.canAccessAllProjects()) {
      return requestRepository.findAll(pageable);
    }

    var accountId = permissionsService.getAccountIdForUser();
    if (accountId.isEmpty()) {
      return Page.empty(pageable);
    }

    var assignmentRequestIds = assignmentFinder.getAllAssignedRequestIds(accountId.get());
    if (!assignmentRequestIds.isEmpty()) {
      return requestRepository.findAll(specFactory.byRequestIds(assignmentRequestIds), pageable);
    }

    var accountProjectIds = projectFinder.getProjectIdsByAccountId(accountId.get());
    if (!accountProjectIds.isEmpty()) {
      return requestRepository.findAll(specFactory.byProjectIds(accountProjectIds), pageable);
    }

    return Page.empty(pageable);
  }

  public Request getRequestOrBatchParentRequestById(Integer requestId) {
    return batchParentRepository
        .getBatchParentIdView(requestId)
        .map(view -> getRequestById(view.getParentRequestIdOrRequestId()))
        .orElseGet(() -> getRequestById(requestId));
  }

  public Request toBatchParentIfChild(Request request) {
    if (!request.isBatchChild()) {
      return request;
    }
    var batchView = getBatchParentIdView(request.getId());
    return getRequestById(batchView.getParentRequestId());
  }

  public BatchParentIdView getBatchParentIdView(Integer requestId) {
    return batchParentRepository
        .getBatchParentIdView(requestId)
        .orElseThrow(() -> toNotFoundException(requestId));
  }

  public Integer getParentRequestIdOrRequestId(Integer requestId) {
    return getBatchParentIdView(requestId).getParentRequestIdOrRequestId();
  }

  public BatchParentIdView getBatchParentIdView(Integer projectId, Integer requestId) {
    return batchParentRepository
        .findByProjectIdAndRequestId(projectId, requestId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Request id %d for project id %d not found", requestId, projectId));
  }

  public void validateRequestExists(Integer requestId) {
    if (!requestRepository.existsById(requestId)) {
      throw toNotFoundException(requestId);
    }
  }

  public Request getRequestById(Integer requestId) {
    return requestRepository.findById(requestId).orElseThrow(() -> toNotFoundException(requestId));
  }

  public Request getRequestAssociatedToDocument(UUID documentId) {
    return getRequestById(getRequestIdAssociatedToDocument(documentId));
  }

  public Integer getRequestIdAssociatedToDocument(UUID documentId) {
    return requestRepository
        .findRequestIdForDocumentId(documentId)
        .orElseThrow(() -> new DocumentNotAssociatedWithRequestException(documentId));
  }

  public Page<Request> getByProjectId(Integer projectId, Pageable pageable) {
    var project = projectFinder.forceGetProjectById(projectId);
    var spec = specFactory.toSpec(project);
    return requestRepository.findAll(spec, pageable);
  }

  public List<Request> getByProjectId(int projectId) {
    return requestRepository.findByProjectId(projectId);
  }

  @Transactional
  public <T> Page<T> findAndMapPage(
      Specification<Request> spec, Pageable pageable, Function<Request, T> conversion) {
    return requestRepository.findAll(spec, pageable).map(conversion);
  }

  public Optional<Integer> findCurrentRequestId(Integer projectId) {
    return requestRepository.findCurrentRequestIdByProjectId(projectId);
  }

  private Collection<Request> getAllSortedRequestsInConsultationFilteredByCompetency(
      Pageable pageable) {
    var sortPageable = PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort());
    var account = permissionsService.forceGetAccountForUser();
    return requestRepository.findAllInConsultation(sortPageable).stream()
        .filter(request -> competenciesFilter.requestMatchesAccountCompetencies(request, account))
        .toList();
  }

  private NotFoundException toNotFoundException(Integer requestId) {
    return new NotFoundException("Request with id %d not found", requestId);
  }
}
