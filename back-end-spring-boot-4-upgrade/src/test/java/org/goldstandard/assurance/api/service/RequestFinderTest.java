package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.jpa.domain.Specification.unrestricted;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.Request;
import org.goldstandard.assurance.api.entity.view.BatchParentIdView;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.repository.BatchParentIdViewRepository;
import org.goldstandard.assurance.api.repository.RequestRepository;
import org.goldstandard.assurance.api.service.account.competencies.AccountCompetenciesFilter;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RequestFinderTest {

  private static final Pageable PAGEABLE = PageRequest.of(0, 5);
  private static final Request REQUEST_ONE = Request.builder().id(1).build();
  private static final Request REQUEST_TWO = Request.builder().id(2).build();
  private static final List<Request> REQUESTS = List.of(REQUEST_ONE, REQUEST_TWO);
  private static final Account ACCOUNT = new Account(3);
  private static final UUID DOCUMENT_ID = UUID.randomUUID();

  @InjectMocks RequestFinder requestFinder;

  @Mock PermissionsService permissionsService;

  @Mock ProjectFinder projectFinder;

  @Mock RequestRepository requestRepository;

  @Mock CompetenciesService competenciesService;

  @Mock AccountCompetenciesFilter competenciesFilter;

  @Mock AssignmentFinder assignmentFinder;

  @Mock BatchParentIdViewRepository batchParentRepository;

  @Mock RequestSpecFactory specFactory;

  @Test
  void requestsInConsultationReturnsAll() {
    var requests = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(requestRepository.findAllInConsultation(PAGEABLE)).thenReturn(requests);

    var requestsInConsultation = requestFinder.getRequestsInConsultation(PAGEABLE).getContent();

    verifyNoInteractions(competenciesService);
    assertThat(requestsInConsultation)
        .hasSize(requests.getContent().size())
        .containsAll(requests.getContent());
  }

  @Test
  void requestsInConsultationByCompetencyReturnsSubset() {
    var requests = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(requestRepository.findAllInConsultation(any(Pageable.class))).thenReturn(requests);
    when(permissionsService.forceGetAccountForUser()).thenReturn(ACCOUNT);
    when(competenciesFilter.requestMatchesAccountCompetencies(REQUEST_ONE, ACCOUNT))
        .thenReturn(true);
    when(competenciesFilter.requestMatchesAccountCompetencies(REQUEST_TWO, ACCOUNT))
        .thenReturn(false);

    var requestsInConsultation =
        requestFinder.getRequestsInConsultationByCompetency(PAGEABLE).getContent();

    assertThat(requestsInConsultation).containsOnly(REQUEST_ONE);
  }

  @Test
  void sortedSinglePageShouldBeUsedToFindAllConsultationsBeforeFilteringByCompetencies() {
    var requests = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(requestRepository.findAllInConsultation(any(Pageable.class))).thenReturn(requests);
    when(permissionsService.forceGetAccountForUser()).thenReturn(ACCOUNT);
    when(competenciesFilter.requestMatchesAccountCompetencies(REQUEST_ONE, ACCOUNT))
        .thenReturn(true);
    when(competenciesFilter.requestMatchesAccountCompetencies(REQUEST_TWO, ACCOUNT))
        .thenReturn(false);

    requestFinder.getRequestsInConsultationByCompetency(PAGEABLE).getContent();

    var captor = ArgumentCaptor.forClass(Pageable.class);
    verify(requestRepository).findAllInConsultation(captor.capture());
    var pageable = captor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(Integer.MAX_VALUE);
    assertThat(pageable.getSort()).isEqualTo(PAGEABLE.getSort());
  }

  @Test
  void shouldReturnAllRequestsIfUserCanAccessAllProjects() {
    when(permissionsService.canAccessAllProjects()).thenReturn(true);
    var requestPage = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(requestRepository.findAll(PAGEABLE)).thenReturn(requestPage);

    requestFinder.getAllRequests(PAGEABLE);

    verifyNoMoreInteractions(permissionsService);
    verifyNoMoreInteractions(requestRepository);
    verifyNoInteractions(projectFinder);
  }

  @Test
  void shouldNoReturnAnyRequestsIfUserCannotAccessAndHasNoAccount() {
    when(permissionsService.canAccessAllProjects()).thenReturn(false);
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.empty());

    var result = requestFinder.getAllRequests(PAGEABLE);

    assertEquals(0, result.getTotalElements());
    verifyNoMoreInteractions(permissionsService);
    verifyNoMoreInteractions(requestRepository);
    verifyNoInteractions(projectFinder);
  }

  @Test
  void shouldReturnAllAssignedRequestsIfUserCannotAssessAllProjectsButIsAssignedToRequests() {
    when(permissionsService.canAccessAllProjects()).thenReturn(false);
    var userAccountId = 1;
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.of(userAccountId));
    var assignedRequestIds = List.of(2, 3);
    when(assignmentFinder.getAllAssignedRequestIds(userAccountId)).thenReturn(assignedRequestIds);
    var spec = mockSpec();
    when(specFactory.byRequestIds(assignedRequestIds)).thenReturn(spec);
    var requestPage = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(requestRepository.findAll(spec, PAGEABLE)).thenReturn(requestPage);

    requestFinder.getAllRequests(PAGEABLE);

    verifyNoMoreInteractions(permissionsService);
    verifyNoMoreInteractions(requestRepository);
    verifyNoInteractions(projectFinder);
  }

  @Test
  void shouldReturnAllAccountRequestsIfUserCannotAccessAllProjectsAndIsNotAssignedToRequests() {
    var requestPage = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    when(permissionsService.canAccessAllProjects()).thenReturn(false);
    var userAccountId = 2;
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.of(userAccountId));
    when(assignmentFinder.getAllAssignedRequestIds(userAccountId))
        .thenReturn(Collections.emptyList());
    var projectIds = List.of(4, 5);
    when(projectFinder.getProjectIdsByAccountId(userAccountId)).thenReturn(projectIds);
    var spec = mockSpec();
    when(specFactory.byProjectIds(projectIds)).thenReturn(spec);
    when(requestRepository.findAll(spec, PAGEABLE)).thenReturn(requestPage);

    requestFinder.getAllRequests(PAGEABLE);

    verifyNoMoreInteractions(permissionsService);
    verifyNoMoreInteractions(requestRepository);
    verifyNoMoreInteractions(projectFinder);
  }

  @Test
  void shouldReturnBatchParentIdViewForRequestId() {
    var requestId = 1;
    var expectedResult = new BatchParentIdView();
    when(batchParentRepository.getBatchParentIdView(requestId))
        .thenReturn(Optional.of(expectedResult));

    var result = requestFinder.getBatchParentIdView(requestId);

    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void shouldReturnBatchParentIdForRequestId() {
    var requestId = 1;
    var batchParent = BatchParentIdView.builder().parentRequestId(2).build();
    when(batchParentRepository.getBatchParentIdView(requestId))
        .thenReturn(Optional.of(batchParent));

    var result = requestFinder.getParentRequestIdOrRequestId(requestId);

    assertThat(result).isEqualTo(batchParent.getParentRequestIdOrRequestId());
  }

  @Test
  void shouldReturnBatchParentIdViewForRequestIdAndProjectId() {
    var projectId = 2;
    var requestId = 3;
    var expectedResult = new BatchParentIdView();
    when(batchParentRepository.findByProjectIdAndRequestId(projectId, requestId))
        .thenReturn(Optional.of(expectedResult));

    var result = requestFinder.getBatchParentIdView(projectId, requestId);

    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void getRequestById() {
    var requestId = 1;
    var expectedRequest = Request.builder().id(requestId).build();
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(expectedRequest));

    var request = requestFinder.getRequestById(requestId);

    assertThat(request).isEqualTo(expectedRequest);
  }

  @Test
  void shouldThrowExceptionIfRequestNotFoundById() {
    var requestId = 5;
    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    var error = catchThrowable(() -> requestFinder.getRequestById(requestId));

    assertThat(error)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Request with id %d not found", requestId);
  }

  @Test
  void shouldReturnOriginalRequestIfNotBatchChild() {
    var original = mock(Request.class);
    when(original.isBatchChild()).thenReturn(false);

    var request = requestFinder.toBatchParentIfChild(original);

    assertThat(request).isEqualTo(original);
  }

  @Test
  void shouldReturnParentRequestIfBatchChild() {
    var child = givenBatchChildRequestWithId(1);
    var parent = givenParentWithId(2, child);

    var request = requestFinder.toBatchParentIfChild(child);

    assertThat(request).isEqualTo(parent);
  }

  @Test
  void shouldReturnProjectRequestPage() {
    var expectedRequestPage = ToPageConverter.toPage(PAGEABLE, REQUESTS);
    var projectId = 3;
    var project = Project.builder().id(projectId).build();
    when(projectFinder.forceGetProjectById(projectId)).thenReturn(project);
    var spec = mockSpec();
    when(specFactory.toSpec(project)).thenReturn(spec);
    when(requestRepository.findAll(spec, PAGEABLE)).thenReturn(expectedRequestPage);

    var requestPage = requestFinder.getByProjectId(projectId, PAGEABLE);

    assertThat(requestPage).isEqualTo(expectedRequestPage);
  }

  @Test
  void shouldReturnAllProjectRequests() {
    var projectId = 4;
    when(requestRepository.findByProjectId(projectId)).thenReturn(REQUESTS);

    var requests = requestFinder.getByProjectId(projectId);

    assertThat(requests).isEqualTo(REQUESTS);
  }

  @Test
  void shouldReturnRequestAssociatedWithDocument() {
    var requestId = REQUEST_ONE.getId();
    when(requestRepository.findRequestIdForDocumentId(DOCUMENT_ID))
        .thenReturn(Optional.of(requestId));
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(REQUEST_ONE));

    assertThat(requestFinder.getRequestAssociatedToDocument(DOCUMENT_ID)).isEqualTo(REQUEST_ONE);

    verify(requestRepository).findRequestIdForDocumentId(DOCUMENT_ID);
    verify(requestRepository).findById(requestId);
  }

  private Request givenBatchChildRequestWithId(int id) {
    var request = mock(Request.class);
    when(request.getId()).thenReturn(id);
    when(request.isBatchChild()).thenReturn(true);
    return request;
  }

  private Request givenParentWithId(int id, Request child) {
    var parentIdView =
        BatchParentIdView.builder().requestId(child.getId()).parentRequestId(id).build();
    when(batchParentRepository.getBatchParentIdView(child.getId()))
        .thenReturn(Optional.of(parentIdView));
    var parent = mock(Request.class);
    when(requestRepository.findById(parentIdView.getParentRequestId()))
        .thenReturn(Optional.of(parent));
    return parent;
  }

  private static Specification<Request> mockSpec() {
    return unrestricted();
  }
}
