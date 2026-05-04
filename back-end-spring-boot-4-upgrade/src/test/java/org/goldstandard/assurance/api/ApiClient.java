package org.goldstandard.assurance.api;

import static org.goldstandard.assurance.api.populator.UserPopulator.ARM_USER_ID;
import static org.goldstandard.assurance.api.populator.UserPopulator.NO_ACCOUNT_USER_ID1;
import static org.goldstandard.assurance.api.populator.UserPopulator.PROJECT_DEVELOPER_USER_ID;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.dto.AccountCreateUpdate;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.AllRequestsDTO;
import org.goldstandard.assurance.api.dto.AssignedRequestDTO;
import org.goldstandard.assurance.api.dto.AssignmentCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.AssignmentStatusUpdate;
import org.goldstandard.assurance.api.dto.AssignmentSummaryResponseDTO;
import org.goldstandard.assurance.api.dto.BatchProjectsDTO;
import org.goldstandard.assurance.api.dto.BatchRequestCreateDTO;
import org.goldstandard.assurance.api.dto.BundleProjectDTO;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.CommentDTO;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestDTO;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.dto.ConsultationDTO;
import org.goldstandard.assurance.api.dto.ConversationDTO;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.DocumentDTO;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
import org.goldstandard.assurance.api.dto.GetBatchCandidateProjectsDTO;
import org.goldstandard.assurance.api.dto.GetBatchTypesDTO;
import org.goldstandard.assurance.api.dto.GetCandidatePOAsRequest;
import org.goldstandard.assurance.api.dto.GetCandidateRealCaseVPAsRequest;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;
import org.goldstandard.assurance.api.dto.InvoiceCreateDTO;
import org.goldstandard.assurance.api.dto.InvoiceDTO;
import org.goldstandard.assurance.api.dto.InvoicePatchDTO;
import org.goldstandard.assurance.api.dto.MessageCreateDTO;
import org.goldstandard.assurance.api.dto.MethodologyHistoryDTO;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO;
import org.goldstandard.assurance.api.dto.ProjectCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.ProjectDeveloperRequestDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryCreateDTO;
import org.goldstandard.assurance.api.dto.ProjectRiskSummaryDTO;
import org.goldstandard.assurance.api.dto.ProjectStatusUpdateDTO;
import org.goldstandard.assurance.api.dto.ProjectTreeNodeDTO;
import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestAccountAssignmentDTO;
import org.goldstandard.assurance.api.dto.RequestActivityDTO;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.dto.RequestFormCreateDTO;
import org.goldstandard.assurance.api.dto.RequestFormDTO;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.dto.RequestFormUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestStatusDetail;
import org.goldstandard.assurance.api.dto.RequestTypeAssignmentTypes;
import org.goldstandard.assurance.api.dto.RequestTypeDocumentTypeDTO;
import org.goldstandard.assurance.api.dto.StatusReportDTO;
import org.goldstandard.assurance.api.dto.TacRequestDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.dto.mfa.*;
import org.goldstandard.assurance.api.entity.ConversationStatus;
import org.goldstandard.assurance.api.entity.Document;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.RequestTypeConsultation;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

@Lazy // allows for initialization after the server port is known
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiClient {

  private final String baseUrl;
  private final ObjectMapper objectMapper;
  private final RestClient restClient;

  @Autowired
  public ApiClient(@LocalServerPort int port, ObjectMapper objectMapper) {
    this(toBaseUrl(port), objectMapper, RestClient.create());
  }

  public ResponseEntity<PagedResource<ProjectDTO>> getFilteredProjects(String role, String filter) {
    var url = String.format("%s?%s", projectsUrl(), filter);
    var entity = getHttpEntityWithTokenForRole(role);
    return getProjects(url, entity);
  }

  public ResponseEntity<PagedResource<ProjectDTO>> getProjects(String role) {
    var url = projectsUrl();
    var entity = getHttpEntityWithTokenForRole(role);
    return getProjects(url, entity);
  }

  public ResponseEntity<PagedResource<ProjectDTO>> getProjects(String role, UUID userId) {
    var url = projectsUrl();
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getProjects(url, entity);
  }

  public ResponseEntity<PagedResource<ProjectDTO>> getProjects(String url, HttpEntity<?> entity) {
    return getPagedResponse(url, entity, ProjectDTO.class);
  }

  public ResponseEntity<Integer> createProject(ProjectCreateDTO dto, String role) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return createProject(entity);
  }

  public ResponseEntity<Integer> createProject(ProjectCreateDTO dto, String role, UUID userId) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return createProject(entity);
  }

  public ResponseEntity<Integer> createProjectUnauthenticated(ProjectCreateDTO dto) {
    var entity = unauthenticated(dto);
    return createProject(entity);
  }

  public ResponseEntity<Integer> createProject(HttpEntity<?> entity) {
    var url = projectsUrl();
    var response = exchange(url, POST, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<ProjectDTO> getProject(Integer id, String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getProject(id, entity);
  }

  public ResponseEntity<ProjectDTO> getProject(Integer id, String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getProject(id, entity);
  }

  public ResponseEntity<ProjectDTO> getProject(Integer id, HttpEntity<?> entity) {
    var url = projectUrl(id);
    return exchange(url, GET, entity, ProjectDTO.class);
  }

  public ResponseEntity<ProjectDTO> getProject(String gsid, String role, UUID userId) {
    var url = String.format("%s/gsid/%s", projectsUrl(), gsid);
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getProject(url, entity);
  }

  public ResponseEntity<ProjectDTO> getProject(String url, HttpEntity<?> entity) {
    return exchange(url, GET, entity, ProjectDTO.class);
  }

  public ResponseEntity<PagedResource<MethodologyHistoryDTO>> getProjectMethodologyHistory(
      Integer projectId, String role) {
    var url = String.format("%s/methodology-history/pages", projectUrl(projectId));
    var entity = getHttpEntityWithTokenForRole(role);
    return getPagedResponse(url, entity, MethodologyHistoryDTO.class);
  }

  public ResponseEntity<ProjectRiskSummaryDTO> createProjectRiskSummary(
      Integer projectId, ProjectRiskSummaryCreateDTO dto, String role, UUID userId) {
    var url = projectRiskSummariesUrl(projectId);
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return exchange(url, POST, entity, ProjectRiskSummaryDTO.class);
  }

  public ResponseEntity<PagedResource<ProjectRiskSummaryDTO>> getProjectRiskSummaries(
      Integer projectId, String role) {
    var url = projectRiskSummariesUrl(projectId);
    var entity = getHttpEntityWithTokenForRole(role);
    return getPagedResponse(url, entity, ProjectRiskSummaryDTO.class);
  }

  public ResponseEntity<Integer> updateProjectUnauthenticated(Integer id, ProjectUpdateDTO patch) {
    var entity = unauthenticated(patch);
    return updateProject(id, entity);
  }

  public ResponseEntity<Integer> updateProject(
      Integer projectId, ProjectUpdateDTO dto, String role, UUID userId) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return updateProject(projectId, entity);
  }

  public ResponseEntity<Integer> updateProject(Integer id, ProjectUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return updateProject(id, entity);
  }

  public ResponseEntity<Integer> updateProject(Integer id, HttpEntity<?> entity) {
    var url = String.format("%s/projects/%d", baseUrl, id);
    var response = exchange(url, PUT, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<PagedResource<CommonDashboardRequestDTO>> getCommonDashboardRequests(
      String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/common-dashboard-requests", baseUrl);
    return getCommonDashboardRequests(url, entity);
  }

  public ResponseEntity<PagedResource<CommonDashboardRequestDTO>>
      getCommonDashboardRequestsWithQuery(String role, String query) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/common-dashboard-requests?%s", baseUrl, query);
    return getPagedResponse(url, entity, CommonDashboardRequestDTO.class);
  }

  public ResponseEntity<Void> exportCommonDashboardReport(String query) {
    return exportCommonDashboardReport(ARM_TEAM_ROLE, ARM_USER_ID, query);
  }

  public ResponseEntity<Void> exportCommonDashboardReport(String role, UUID userId, String query) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/common-dashboard-requests/reports?%s", baseUrl, query);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<StatusReportDTO> getStatusReport(String role) {
    return getStatusReport(role, null, null);
  }

  public ResponseEntity<StatusReportDTO> getStatusReport(
      String role, String startDate, String endDate) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = statusReportUrl(startDate, endDate);
    return exchange(url, GET, entity, StatusReportDTO.class);
  }

  public ResponseEntity<byte[]> getStatusReportExcel(String role) {
    return getStatusReportExcel(role, null, null);
  }

  public ResponseEntity<byte[]> getStatusReportExcel(
      String role, String startDate, String endDate) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = statusReportExcelUrl(startDate, endDate);
    return getFile(url, entity);
  }

  public ResponseEntity<List<Integer>> updateProjectStatus(
      String role, Integer projectId, ProjectStatusUpdateDTO update) {
    var entity = getHttpEntityWithTokenAndBodyForRole(update, role);
    var url = projectStatusUrl(projectId);
    var response = exchange(url, PUT, entity, String.class);
    return toParameterizedListResponse(response, Integer.class);
  }

  public ResponseEntity<InvoiceDTO> getInvoiceUnauthenticated(Integer invoiceId) {
    var entity = unauthenticated();
    return getInvoice(entity, invoiceId);
  }

  public ResponseEntity<InvoiceDTO> getInvoice(String role, Integer invoiceId) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getInvoice(entity, invoiceId);
  }

  public ResponseEntity<InvoiceDTO> getInvoice(HttpEntity<?> entity, Integer invoiceId) {
    var url = invoicesUrl(invoiceId);
    return exchange(url, GET, entity, InvoiceDTO.class);
  }

  public ResponseEntity<InvoiceDTO> createInvoiceUnauthenticated(InvoiceCreateDTO dto) {
    var entity = unauthenticated(dto);
    return createInvoice(entity);
  }

  public ResponseEntity<InvoiceDTO> createInvoice(String role, InvoiceCreateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return createInvoice(entity);
  }

  public ResponseEntity<InvoiceDTO> createInvoice(HttpEntity<?> entity) {
    var url = invoicesUrl();
    return exchange(url, POST, entity, InvoiceDTO.class);
  }

  public ResponseEntity<InvoiceDTO> patchInvoiceUnauthenticated(
      Integer invoiceId, InvoicePatchDTO dto) {
    var entity = unauthenticated(dto);
    return patchInvoice(entity, invoiceId);
  }

  public ResponseEntity<InvoiceDTO> patchInvoice(
      String role, Integer invoiceId, InvoicePatchDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return patchInvoice(entity, invoiceId);
  }

  public ResponseEntity<InvoiceDTO> patchInvoice(HttpEntity<?> entity, Integer invoiceId) {
    var url = invoicesUrl(invoiceId);
    return exchange(url, PATCH, entity, InvoiceDTO.class);
  }

  public ResponseEntity<List<ProjectDTO>> getBatchRequestCandidateProjects(
      GetBatchCandidateProjectsDTO body) {
    var url = String.format("%s/candidate-projects", batchRequestsUrl());
    var entity = getHttpEntityWithTokenAndBodyForRole(body, ADMIN_ROLE);
    var response = exchange(url, POST, entity, String.class);
    return toParameterizedListResponse(response, ProjectDTO.class);
  }

  public ResponseEntity<Integer> createBatchRequest(BatchRequestCreateDTO createDTO) {
    var entity = getHttpEntityWithTokenAndBodyForRole(createDTO, ADMIN_ROLE);
    var url = batchRequestsUrl();
    var response = exchange(url, POST, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<List<BatchType>> getBatchTypes(GetBatchTypesDTO getDTO) {
    var entity = this.getHttpEntityWithTokenAndBodyForRole(getDTO, ADMIN_ROLE);
    var url = String.format("%s/types", batchRequestsUrl());
    var response = exchange(url, POST, entity, String.class);
    return toParameterizedListResponse(response, BatchType.class);
  }

  public ResponseEntity<BatchProjectsDTO> getPublicBatchProjects(Integer batchId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/public/batch/%d/projects", baseUrl, batchId);
    return exchange(url, GET, entity, BatchProjectsDTO.class);
  }

  public ResponseEntity<BatchProjectsDTO> getBatchProjects(Integer batchId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/%d/projects", batchRequestsUrl(), batchId);
    return exchange(url, GET, entity, BatchProjectsDTO.class);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsByProjectId(Integer projectId) {
    var entity = getHttpEntityWithTokenForRole(ARM_TEAM_ROLE);
    var url = String.format("%s?projectId=%d", requestsUrl(), projectId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestDTO.class);
  }

  public ResponseEntity<PagedResource<ProjectDeveloperRequestDTO>> getProjectDeveloperRequests(
      GetProjectDeveloperRequestsDTO dto) {
    var entity =
        getHttpEntityWithTokenAndBodyForRoleWithId(
            dto, PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);
    var url = String.format("%s/project-developer-requests/pages", baseUrl);
    var response = exchange(url, POST, entity, String.class);
    return toPagedResourceResponse(response, ProjectDeveloperRequestDTO.class);
  }

  public ResponseEntity<PagedResource<TacRequestDTO>> getTacRequests() {
    var entity = getHttpEntityWithTokenForRole(TAC_ROLE);
    var url = String.format("%s/requests/consultation", baseUrl);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, TacRequestDTO.class);
  }

  public ResponseEntity<String> getPublicConsultations() {
    var entity = unauthenticated();
    var url = String.format("%s/public/consultations/pages", baseUrl);
    return exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<String> getPublicConsultation(String gsid) {
    var entity = unauthenticated();
    var url = String.format("%s/public/consultations/%s", baseUrl, gsid);
    return exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<Integer> publicCreateComment(CommentCreateDTO dto) {
    var entity = unauthenticated(dto);
    var url = String.format("%s/public/comments", baseUrl);
    var response = exchange(url, POST, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<String> publicGetRequestProjectGsId(int id) {
    var entity = unauthenticated();
    var url = String.format("%s/public/requests/%d/gsid", baseUrl, id);
    return exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<List<ProjectDTO>> getProjectDeveloperRequestProjects() {
    var entity =
        getHttpEntityWithTokenForRoleWithId(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);
    var url = String.format("%s/project-developer-requests/projects", baseUrl);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, ProjectDTO.class);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsForRole(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getRequestsForRole(entity);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsForUser(String role, UUID idpUserId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, idpUserId);
    return getRequestsForRole(entity);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsUnauthenticated() {
    return getRequestsForRole(unauthenticated());
  }

  public ResponseEntity<RequestDTO> getRequestById(int id) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestByIdResponse(id, entity);
  }

  public ResponseEntity<RequestDTO> getRequestById(String role, int id, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getRequestByIdResponse(id, entity);
  }

  public ResponseEntity<Integer> createRequest(int projectId, RequestCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    var url = projectRequestsUrl(projectId);
    var response = exchange(url, POST, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<Integer> updateRequest(
      int projectId, RequestCreateUpdateDTO dto, String role, UUID idpUserId) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, idpUserId);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> armUpdateRequest(int projectId, RequestCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, ARM_TEAM_ROLE, ARM_USER_ID);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> updateRequest(int projectId, RequestCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> updateRequestUnauthenticated(
      int projectId, RequestCreateUpdateDTO dto) {
    return updateRequest(projectId, unauthenticated(dto));
  }

  public ResponseEntity<Integer> updateRequest(int projectId, HttpEntity<?> entity) {
    var url = projectRequestsUrl(projectId);
    var response = exchange(url, PUT, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<Map<String, List<ReferenceDataType>>> getReferenceDataTypes() {
    var entity = getHttpEntityWithToken();
    return getReferenceDataTypes(entity);
  }

  public ResponseEntity<Map<String, List<ReferenceDataType>>>
      getReferenceDataTypesUnauthenticated() {
    var entity = unauthenticated();
    return getReferenceDataTypes(entity);
  }

  public ResponseEntity<Map<String, List<ReferenceDataType>>> getReferenceDataTypes(
      HttpEntity<?> entity) {
    var url = String.format("%s/reference-data-types", baseUrl);
    return restClient
        .get()
        .uri(url)
        .headers((headers) -> headers.addAll(entity.getHeaders()))
        .retrieve()
        .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<Map<String, List<RequestTypeDocumentTypeDTO>>> getRequiredDocumentTypes(
      int requestTypeId) {
    var entity = getHttpEntityWithToken();
    var url = String.format("%s/requests/required-document-types/%d", baseUrl, requestTypeId);
    return restClient
        .get()
        .uri(url)
        .headers((headers) -> headers.addAll(entity.getHeaders()))
        .retrieve()
        .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<AccountDTO>> getAccounts(String role, String queryString) {
    var url = String.format("%s?%s", accountsUrl(), queryString);
    var entity = getHttpEntityWithTokenForRole(role);
    return getAccounts(url, entity);
  }

  public ResponseEntity<PagedResource<AccountDTO>> getAccountsUnauthenticated() {
    var entity = unauthenticated();
    return getAccounts(entity);
  }

  public ResponseEntity<PagedResource<AccountDTO>> getAccounts(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getAccounts(entity);
  }

  public ResponseEntity<PagedResource<AccountDTO>> getAccounts(HttpEntity<?> entity) {
    var url = accountsUrl();
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, AccountDTO.class);
  }

  public ResponseEntity<PagedResource<AccountDTO>> getAccounts(String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, AccountDTO.class);
  }

  public ResponseEntity<Integer> createAccount(String role, AccountCreateUpdate dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return createAccount(entity);
  }

  public ResponseEntity<Integer> createAccountUnauthenticated(AccountCreateUpdate dto) {
    var entity = unauthenticated(dto);
    return createAccount(entity);
  }

  public ResponseEntity<Integer> createAccount(HttpEntity<?> entity) {
    var url = accountsUrl();
    var response = exchange(url, POST, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<AccountDTO> getAccount(String role, int id) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getAccount(id, entity);
  }

  public ResponseEntity<AccountDTO> getAccount(String role, UUID userId, int id) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getAccount(id, entity);
  }

  public ResponseEntity<AccountDTO> getAccountUnauthenticated(int id) {
    var entity = unauthenticated();
    return getAccount(id, entity);
  }

  public ResponseEntity<AccountDTO> getAccount(int id, HttpEntity<?> entity) {
    var url = accountUrl(id);
    return exchange(url, GET, entity, AccountDTO.class);
  }

  public ResponseEntity<Void> updateAccount(String role, Integer id, AccountCreateUpdate dto) {
    var url = accountUrl(id);
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return exchange(url, PUT, entity, Void.class);
  }

  public ResponseEntity<Integer> createCompetencies(int accountId, CompetenciesDTO dto) {
    return upsertCompetencies(accountId, dto, POST);
  }

  public ResponseEntity<Integer> updateCompetencies(int accountId, CompetenciesDTO dto) {
    return upsertCompetencies(accountId, dto, PUT);
  }

  public ResponseEntity<CompetenciesDTO> getCompetencies(Integer accountId) {
    var url = accountCompetenciesUrl(accountId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return exchange(url, GET, entity, CompetenciesDTO.class);
  }

  private ResponseEntity<Integer> upsertCompetencies(
      Integer accountId, CompetenciesDTO dto, HttpMethod method) {
    var url = accountCompetenciesUrl(accountId);
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return exchange(url, method, entity, Integer.class);
  }

  public ResponseEntity<PagedResource<UserDTO>> getUserPage(String role) {
    var url = getUserPageUrl();
    var entity = getHttpEntityWithTokenForRole(role);
    return getUserPage(url, entity);
  }

  public ResponseEntity<List<UserDTO>> getAllUsers(String role) {
    var url = getUsersUrl();
    var entity = getHttpEntityWithTokenForRole(role);
    return getUsers(url, entity);
  }

  public ResponseEntity<List<UserDTO>> getAllUsersUnauthenticated() {
    var url = getUsersUrl();
    return getUsers(url, unauthenticated());
  }

  public ResponseEntity<List<UserDTO>> getAllActiveUsers(String role) {
    var url = getActiveUsersUrl();
    var entity = getHttpEntityWithTokenForRole(role);
    return getUsers(url, entity);
  }

  public ResponseEntity<String> getUser(
      UUID idpUserIdToGet, String role, UUID authenticatedIpdUserId) {
    return getUser(
        idpUserIdToGet, getHttpEntityWithTokenForRoleWithId(role, authenticatedIpdUserId));
  }

  public ResponseEntity<List<UserDTO>> searchUsers(String searchText, String role) {
    var url = searchUsersUrl(searchText);
    var entity = getHttpEntityWithTokenForRole(role);
    return getUsers(url, entity);
  }

  public ResponseEntity<UserBatch> createUserBatch(
      String role, MultiValueMap<String, Object> data) {
    var url = userBatchesUrl();
    var entity = getHttpEntityWithTokenAndFormBodyForRoleWithId(data, role, NO_ACCOUNT_USER_ID1);
    return exchange(url, POST, entity, UserBatch.class);
  }

  public ResponseEntity<UserBatch> getUserBatch(int id) {
    var entity = getHttpEntityWithTokenForRoleWithId(ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    return getUserBatch(id, entity);
  }

  public ResponseEntity<UserBatch> getUserBatch(String role, int id) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, NO_ACCOUNT_USER_ID1);
    return getUserBatch(id, entity);
  }

  public ResponseEntity<UserBatch> getUserBatch(int id, HttpEntity<?> entity) {
    var url = String.format("%s/%d", userBatchesUrl(), id);
    return exchange(url, GET, entity, UserBatch.class);
  }

  public ResponseEntity<RequestStatusDetail> getRequestStatusDetail(Integer id) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestStatusDetail(id, entity);
  }

  public ResponseEntity<RequestStatusDetail> getRequestStatusDetailUnauthenticated(Integer id) {
    var entity = unauthenticated();
    return getRequestStatusDetail(id, entity);
  }

  public ResponseEntity<RequestStatusDetail> getRequestStatusDetail(
      Integer id, HttpEntity<?> entity) {
    var url = String.format("%s/request-status/%d", baseUrl, id);
    return exchange(url, GET, entity, RequestStatusDetail.class);
  }

  public ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequestsForUser(
      String role, UUID idpUserId) {
    var url = assignedRequestsUrl();
    var entity = getHttpEntityWithTokenForRoleWithId(role, idpUserId);
    return getAssignedRequests(url, entity);
  }

  public ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequests() {
    return getAssignedRequests(assignedRequestsUrl(), getArmUserRequestEntity());
  }

  public ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequestsForUser(
      String role, UUID idpUserId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, idpUserId);
    return getAllRequests(entity);
  }

  public ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests() {
    return getAllRequests(getArmUserRequestEntity());
  }

  public ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequestsWithQuery(
      String query) {
    var url = String.format("%s?%s", assignedRequestsUrl(), query);
    var entity = getArmUserRequestEntity();
    return getAssignedRequests(url, entity);
  }

  public ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequestsWithQuery(String query) {
    var url = String.format("%s?%s", allRequestsUrl(), query);
    var entity = getArmUserRequestEntity();
    return getAllRequests(url, entity);
  }

  public ResponseEntity<List<ProjectDTO>> getAssignedRequestProjects() {
    var url = assignedRequestProjectsUrl();
    var entity = getArmUserRequestEntity();
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, ProjectDTO.class);
  }

  public ResponseEntity<List<ExtendedReferenceDataType>> getAssignedRequestStatuses() {
    var url = assignedRequestStatusesUrl();
    var entity = getArmUserRequestEntity();
    return getReferenceDataTypes(url, entity);
  }

  public ResponseEntity<List<ExtendedReferenceDataType>> getAssignedRequestTypes() {
    var url = assignedRequestTypesUrl();
    var entity = getArmUserRequestEntity();
    return getReferenceDataTypes(url, entity);
  }

  public ResponseEntity<List<ExtendedReferenceDataType>> getAssignedRequestInvoiceStatuses() {
    var url = assignedRequestInvoiceStatusesUrl();
    var entity = getArmUserRequestEntity();
    return getReferenceDataTypes(url, entity);
  }

  public ResponseEntity<List<AccountDTO>> getAssignedRequestAccounts(int accountTypeId) {
    var url = assignedRequestAccountsUrl(accountTypeId);
    var entity = getArmUserRequestEntity();
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, AccountDTO.class);
  }

  public ResponseEntity<ProblemDetail> getAssignedRequestAccountsError(int accountTypeId) {
    var url = assignedRequestAccountsUrl(accountTypeId);
    var entity = getArmUserRequestEntity();
    return restClient
        .get()
        .uri(url)
        .headers((headers) -> headers.addAll(entity.getHeaders()))
        .retrieve()
        .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
        .toEntity(ProblemDetail.class);
  }

  public ResponseEntity<RequestTypeAssignmentTypes> getAssignmentTypes(
      String role, Integer requestTypeId) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getAssignmentTypes(requestTypeId, entity);
  }

  public ResponseEntity<RequestTypeAssignmentTypes> getAssignmentTypesUnauthenticated(
      Integer requestTypeId) {
    var entity = unauthenticated();
    return getAssignmentTypes(requestTypeId, entity);
  }

  public ResponseEntity<RequestTypeAssignmentTypes> getAssignmentTypes(
      Integer requestTypeId, HttpEntity<?> entity) {
    var url = String.format("%s/request-type-assignment-types/%d", baseUrl, requestTypeId);
    return exchange(url, GET, entity, RequestTypeAssignmentTypes.class);
  }

  public ResponseEntity<List<RequestTypeConsultation>> getRequestTypesConsultationRequired(
      String role) {
    var entity = this.getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/request-type-consultations", baseUrl);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, RequestTypeConsultation.class);
  }

  public ResponseEntity<PagedResource<BundleProjectDTO>> getBundleProjects(Integer bundleId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/bundles/%d/projects/pages", baseUrl, bundleId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, BundleProjectDTO.class);
  }

  public ResponseEntity<List<Project>> getCandidatePOAs(
      String role, GetCandidatePOAsRequest request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/projects/candidate-poas", baseUrl);
    var response = exchange(url, POST, entity, String.class);
    return toParameterizedListResponse(response, Project.class);
  }

  public ResponseEntity<List<Project>> getCandidateRealCaseVPAs(
      String role, GetCandidateRealCaseVPAsRequest request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/projects/candidate-real-case-vpas", baseUrl);
    var response = exchange(url, POST, entity, String.class);
    return toParameterizedListResponse(response, Project.class);
  }

  public ResponseEntity<PagedResource<RequestActivityDTO>> getRequestActivityHistory(
      Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/requests/%d/activities/pages", baseUrl, requestId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestActivityDTO.class);
  }

  public ResponseEntity<List<PathwayGroupTimelineActivityDTO>> getRequestPathwayGroupTimeline(
      Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/requests/%d/current-pathway-group-timelines", baseUrl, requestId);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, PathwayGroupTimelineActivityDTO.class);
  }

  public ResponseEntity<Void> closeActivity(Integer requestActivityId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/request-activities/%d/completions", baseUrl, requestActivityId);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<PagedResource<DocumentDTO>> getRequestDocuments(
      String role, UUID userId, Integer projectId, Integer requestId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getRequestDocuments(entity, projectId, requestId);
  }

  public ResponseEntity<PagedResource<DocumentDTO>> getRequestDocuments(
      Integer projectId, Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestDocuments(entity, projectId, requestId);
  }

  public ResponseEntity<PagedResource<DocumentDTO>> getRequestDocumentsByDisplayCategoryId(
      Integer projectId, Integer requestId, Integer displayCategoryId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestDocumentsByDisplayCategoryId(entity, projectId, requestId, displayCategoryId);
  }

  public ResponseEntity<PagedResource<DocumentDTO>> getRequestDocumentsUnauthenticated(
      Integer projectId, Integer requestId) {
    var entity = unauthenticated();
    return getRequestDocuments(entity, projectId, requestId);
  }

  public ResponseEntity<String> getPublicProjectDocuments(String gsid) {
    var entity = unauthenticated();
    var url = String.format("%s/public/project-documents/%s", baseUrl, gsid);
    return exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<byte[]> getPublicDocument(UUID documentId) {
    var entity = unauthenticated();
    return getPublicDocument(entity, documentId);
  }

  public ResponseEntity<byte[]> downloadDocumentUnauthenticated(UUID documentId) {
    var entity = unauthenticated();
    return downloadDocument(entity, documentId);
  }

  public ResponseEntity<byte[]> downloadDocument(String role, UUID documentId) {
    var entity = getHttpEntityWithTokenForRole(role);
    return downloadDocument(entity, documentId);
  }

  public ResponseEntity<byte[]> downloadDocument(String role, UUID userId, UUID documentId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return downloadDocument(entity, documentId);
  }

  public ResponseEntity<UUID> uploadDocumentUnauthenticated(
      String filePath,
      String uploadName,
      Integer projectId,
      Integer requestId,
      Integer documentTypeId) {
    var headers = new HttpHeaders();
    return uploadDocument(headers, filePath, uploadName, projectId, requestId, documentTypeId);
  }

  public ResponseEntity<UUID> uploadDocument(
      String role,
      UUID userId,
      String filePath,
      String uploadName,
      Integer projectId,
      Integer requestId,
      Integer documentTypeId) {
    var headers = getHttpHeadersWithToken(role, userId);
    return uploadDocument(headers, filePath, uploadName, projectId, requestId, documentTypeId);
  }

  public ResponseEntity<UUID> uploadDocument(
      String role,
      String filePath,
      String uploadName,
      Integer projectId,
      Integer requestId,
      Integer documentTypeId) {
    var headers = getHttpHeadersWithToken(role, null);
    return uploadDocument(headers, filePath, uploadName, projectId, requestId, documentTypeId);
  }

  public ResponseEntity<Document> updateDocumentUnauthenticated(
      UUID documentId, DocumentUpdate update) {
    var entity = unauthenticated(update);
    return updateDocument(entity, documentId);
  }

  public ResponseEntity<Document> updateDocument(
      String role, UUID documentId, DocumentUpdate update) {
    var entity = getHttpEntityWithTokenAndBodyForRole(update, role);
    return updateDocument(entity, documentId);
  }

  public ResponseEntity<Document> updateDocument(
      String role, UUID userId, UUID documentId, DocumentUpdate update) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(update, role, userId);
    return updateDocument(entity, documentId);
  }

  public ResponseEntity<Void> deleteRequestDocumentUnauthenticated(
      Integer requestId, UUID documentId) {
    var entity = unauthenticated();
    return deleteRequestDocument(entity, requestId, documentId);
  }

  public ResponseEntity<Void> deleteRequestDocument(
      String role, Integer requestId, UUID documentId) {
    var entity = getHttpEntityWithTokenForRole(role);
    return deleteRequestDocument(entity, requestId, documentId);
  }

  public ResponseEntity<Void> deleteRequestDocument(
      HttpEntity<?> entity, Integer requestId, UUID documentId) {
    var url =
        String.format("%s/requests/%s/documents/%s", baseUrl, requestId, documentId.toString());
    return exchange(url, DELETE, entity, Void.class);
  }

  public ResponseEntity<PagedResource<ProjectTreeNodeDTO>> getProjectTrees(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/projects/trees/pages", baseUrl);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ProjectTreeNodeDTO.class);
  }

  public ResponseEntity<ProjectTreeNodeDTO> getProjectTree(String role, Integer projectId) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/projects/%d/trees", baseUrl, projectId);
    return exchange(url, GET, entity, ProjectTreeNodeDTO.class);
  }

  public ResponseEntity<PagedResource<MethodologyManagementDTO>> getAllMethodologiesPagedAsAdmin() {
    return getAllMethodologiesPagedAsAdmin("");
  }

  public ResponseEntity<PagedResource<MethodologyManagementDTO>> getAllMethodologiesPagedAsAdmin(
      String description) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/methodologies/pages?description=%s", baseUrl, description);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, MethodologyManagementDTO.class);
  }

  public ResponseEntity<List<Methodology>> getAllMethodologiesAs(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/methodologies", baseUrl);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, Methodology.class);
  }

  public ResponseEntity<MethodologyManagementDTO> createMethodologyAsAdmin(
      MethodologyManagementDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    var url = String.format("%s/methodologies", baseUrl);
    return exchange(url, POST, entity, MethodologyManagementDTO.class);
  }

  public ResponseEntity<MethodologyManagementDTO> updateMethodologyAsAdmin(
      MethodologyManagementDTO dto, Integer id) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    var url = String.format("%s/methodologies/%s", baseUrl, id);
    return exchange(url, PUT, entity, MethodologyManagementDTO.class);
  }

  public ResponseEntity<Void> deleteMethodologyAsAdmin(Integer id) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/methodologies/%s", baseUrl, id);
    return exchange(url, DELETE, entity, Void.class);
  }

  public ResponseEntity<List<CommentDTO>> getRequestComments(Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/comments/%d", baseUrl, requestId);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, CommentDTO.class);
  }

  public ResponseEntity<Boolean> checkDocumentSupportedUnauthenticated(
      String contentType, Long fileSizeBytes) {
    var entity = unauthenticated();
    return checkDocumentSupported(entity, contentType, fileSizeBytes);
  }

  public ResponseEntity<Boolean> checkDocumentSupported(
      String role, String contentType, Long fileSizeBytes) {
    var entity = getHttpEntityWithTokenForRole(role);
    return checkDocumentSupported(entity, contentType, fileSizeBytes);
  }

  public ResponseEntity<Boolean> checkDocumentSupported(
      HttpEntity<?> entity, String contentType, Long fileSizeBytes) {
    var url =
        String.format(
            "%s/pii-analysis/documents/supported?contentType=%s&documentSizeBytes=%s",
            baseUrl, contentType, fileSizeBytes.toString());
    return exchange(url, GET, entity, Boolean.class);
  }

  public ResponseEntity<TextAnalysisResultsDTO> analyseText(String role, TextAnalysisDTO request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/pii-analysis", baseUrl);
    return exchange(url, POST, entity, TextAnalysisResultsDTO.class);
  }

  public ResponseEntity<TextAnalysisResultsDTO> publicAnalyseText(TextAnalysisDTO request) {
    var entity = unauthenticated(request);
    var url = String.format("%s/public/pii-analysis", baseUrl);
    return exchange(url, POST, entity, TextAnalysisResultsDTO.class);
  }

  public ResponseEntity<Void> startDocumentAnalysisUnauthenticated(UUID documentId) {
    var entity = unauthenticated();
    return startDocumentAnalysis(entity, documentId);
  }

  public ResponseEntity<Void> startDocumentAnalysis(String role, UUID userId, UUID documentId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return startDocumentAnalysis(entity, documentId);
  }

  public ResponseEntity<Void> startDocumentAnalysis(HttpEntity<?> entity, UUID documentId) {
    var url = String.format("%s/pii-analysis/documents/%s", baseUrl, documentId);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<PagedResource<PageGroupedPii>> getDocumentPiiAnalysisResultsUnauthenticated(
      UUID documentId) {
    var entity = unauthenticated();
    return getDocumentPiiAnalysisResults(entity, documentId);
  }

  public ResponseEntity<PagedResource<PageGroupedPii>> getDocumentPiiAnalysisResults(
      String role, UUID documentId) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getDocumentPiiAnalysisResults(entity, documentId);
  }

  public ResponseEntity<PagedResource<PageGroupedPii>> getDocumentPiiAnalysisResults(
      HttpEntity<?> entity, UUID documentId) {
    var url = String.format("%s/pii-analysis/documents/%s/results", baseUrl, documentId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, PageGroupedPii.class);
  }

  public ResponseEntity<ConversationDTO> openConversation(int projectId, int requestId) {
    var entity = getArmUserRequestEntity();
    var url = conversationsUrl(projectId, requestId);
    return exchange(url, POST, entity, ConversationDTO.class);
  }

  public ResponseEntity<ConversationDTO> updateConversation(
      int projectId, int requestId, int conversationId, ConversationStatus status) {
    var url = String.format("%s/%d/status", conversationsUrl(projectId, requestId), conversationId);
    var entity = getHttpEntityWithTokenAndBodyForRole(status, ARM_TEAM_ROLE);
    return exchange(url, PUT, entity, ConversationDTO.class);
  }

  public ResponseEntity<ConversationMessageDTO> sendMessage(
      int projectId, int requestId, int conversationId, MessageCreateDTO message) {
    var url = conversationMessagesUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(message, ARM_TEAM_ROLE, ARM_USER_ID);
    return exchange(url, POST, entity, ConversationMessageDTO.class);
  }

  public ResponseEntity<PagedResource<ConversationMessageDTO>> getMessages(
      int projectId, int requestId, int conversationId) {
    var url = conversationMessagePagesUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenForRoleWithId(ARM_TEAM_ROLE, ARM_USER_ID);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ConversationMessageDTO.class);
  }

  public ResponseEntity<Void> markMessagesAsRead(int projectId, int requestId, int conversationId) {
    var url = conversationMessageReadsUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenForRoleWithId(ARM_TEAM_ROLE, ARM_USER_ID);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<PagedResource<RequestAccountAssignmentDTO>> getAssignments(
      String role, UUID userId, Integer accountId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getAssignments(entity, accountId, "");
  }

  public ResponseEntity<PagedResource<RequestAccountAssignmentDTO>> getAssignments(
      Integer accountId, String query) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getAssignments(entity, accountId, query);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> getAssignment(Integer id) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getAssignment(entity, id);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> getAssignment(
      String role, UUID userId, Integer id) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getAssignment(entity, id);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> createAssignment(
      String role, AssignmentCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return createAssignment(entity);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> createAssignment(
      String role, UUID userId, AssignmentCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return createAssignment(entity);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> createAssignmentUnauthenticated(
      AssignmentCreateUpdateDTO dto) {
    var entity = unauthenticated(dto);
    return createAssignment(entity);
  }

  private ResponseEntity<RequestAccountAssignmentDTO> createAssignment(HttpEntity<?> entity) {
    var url = String.format("%s/assignments", baseUrl);
    return exchange(url, POST, entity, RequestAccountAssignmentDTO.class);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> updateAssignment(
      String role, Integer id, AssignmentCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return updateAssignment(entity, id);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> updateAssignment(
      String role, UUID userId, Integer id, AssignmentCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return updateAssignment(entity, id);
  }

  public ResponseEntity<RequestAccountAssignmentDTO> updateAssignmentUnauthenticated(
      Integer id, AssignmentCreateUpdateDTO dto) {
    var entity = unauthenticated(dto);
    return updateAssignment(entity, id);
  }

  public ResponseEntity<List<RequestAccountAssignmentDTO>> getRequestAssignments(
      String role, UUID userId, Integer requestId) {
    return getRequestAssignments(role, userId, requestId, "");
  }

  public ResponseEntity<List<RequestAccountAssignmentDTO>> getRequestAssignments(
      String role, UUID userId, Integer requestId, String query) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/requests/%d/assignments%s", baseUrl, requestId, query);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, RequestAccountAssignmentDTO.class);
  }

  public ResponseEntity<String> updateAssignmentStatus(
      String role, UUID userId, Integer id, AssignmentStatusUpdate dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return updateAssignmentStatus(entity, id);
  }

  public ResponseEntity<String> updateAssignmentStatus(
      String role, Integer id, AssignmentStatusUpdate dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return updateAssignmentStatus(entity, id);
  }

  public ResponseEntity<String> updateAssignmentStatusUnauthenticated(
      Integer id, AssignmentStatusUpdate dto) {
    var entity = unauthenticated(dto);
    return updateAssignmentStatus(entity, id);
  }

  public ResponseEntity<AssignmentSummaryResponseDTO> getArmAssignmentSummary(
      String filter, String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/arm?%s", getAssignmentSummariesUrl(), filter);
    return exchange(url, GET, entity, AssignmentSummaryResponseDTO.class);
  }

  public ResponseEntity<AssignmentSummaryResponseDTO> getReviewAssignmentSummary(
      String filter, String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/review?%s", getAssignmentSummariesUrl(), filter);
    return exchange(url, GET, entity, AssignmentSummaryResponseDTO.class);
  }

  public ResponseEntity<String> getActuatorHealth() {
    return getActuator("health");
  }

  public ResponseEntity<String> getActuatorInfo() {
    return getActuator("info");
  }

  public ResponseEntity<String> getActuator(String endpoint) {
    var url = actuatorUrl(endpoint);
    var entity = new HttpEntity<>(new HttpHeaders());
    return exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<MfaSettingsDTO> getMfaSettings(String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return exchange(mfaSettingsUrl(), GET, entity, MfaSettingsDTO.class);
  }

  public ResponseEntity<MfaSettingsDTO> getMfaSettingsAsAdmin(String targetUsername) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s?username=%s", mfaSettingsUrl(), targetUsername);
    return exchange(url, GET, entity, MfaSettingsDTO.class);
  }

  public ResponseEntity<UpdateMfaSettingsResponseDTO> updateMfaSettings(
      String role, UUID userId, MfaSettingsDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    var url = mfaSettingsUrl();
    return exchange(url, PUT, entity, UpdateMfaSettingsResponseDTO.class);
  }

  public ResponseEntity<UpdateMfaSettingsResponseDTO> updateMfaSettings(
      String role, MfaSettingsDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    var url = mfaSettingsUrl();
    return exchange(url, PUT, entity, UpdateMfaSettingsResponseDTO.class);
  }

  public ResponseEntity<SoftwareTokenSetupResponseDTO> setupSoftwareToken(
      String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = mfaSoftwareTokenSetupUrl();
    return exchange(url, POST, entity, SoftwareTokenSetupResponseDTO.class);
  }

  public ResponseEntity<VerifySoftwareTokenResponseDTO> verifySoftwareToken(
      String role, UUID userId, VerifySoftwareTokenRequestDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    var url = mfaSoftwareTokenVerifyUrl();
    return exchange(url, POST, entity, VerifySoftwareTokenResponseDTO.class);
  }

  public ResponseEntity<Void> updatePiiToNotFound(
      Integer projectId, Collection<String> projectFields) {
    var entity =
        getHttpEntityWithTokenAndBodyForRoleWithId(projectFields, ARM_TEAM_ROLE, ARM_USER_ID);
    var url = String.format("%s/pii-metadata/%s/action/not-found", baseUrl, projectId);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<Void> issueRequestSalesOrder(Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/requests/%d/sales-orders", baseUrl, requestId);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<Void> issueProjectContract(String gsId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/projects/%s/contracts", baseUrl, gsId);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<Void> publishAllRegistryUpdates(String role) {
    var url = registryUpdateUrl();
    return publishRegistryUpdate(role, url);
  }

  public ResponseEntity<Void> publishRegistryUpdate(String role, int projectId) {
    var url = String.format("%s/%d", registryUpdateUrl(), projectId);
    return publishRegistryUpdate(role, url);
  }

  public ResponseEntity<Void> publishRegistryUpdate(String role, String url) {
    var entity = getHttpEntityWithTokenForRole(role);
    return exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<Void> completeConsultationsForRequest(int requestId) {
    var url = toConsultationCompletionUrl(requestId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return exchange(url, PUT, entity, Void.class);
  }

  public ResponseEntity<List<ConsultationDTO>> getConsultationsForRequest(int requestId) {
    var url = toConsultationsUrl(requestId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, ConsultationDTO.class);
  }

  public ResponseEntity<PagedResource<ConsultationDTO>> getPageOfConsultations(
      ZonedDateTime liveAt) {
    return getPageOfConsultations(liveAt, null);
  }

  public ResponseEntity<PagedResource<ConsultationDTO>> getPageOfConsultations(
      ZonedDateTime liveAt, Integer typeId) {
    var url = toPagedConsultationsUrl(liveAt, typeId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ConsultationDTO.class);
  }

  public ResponseEntity<FormDefinitionDTO> createFormDefinition(FormDefinitionCreateDTO dto) {
    return createFormDefinition(dto, ADMIN_ROLE);
  }

  public ResponseEntity<FormDefinitionDTO> createFormDefinition(
      FormDefinitionCreateDTO dto, String role) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return createFormDefinition(entity);
  }

  public ResponseEntity<FormDefinitionDTO> createFormDefinitionUnauthenticated(
      FormDefinitionCreateDTO dto) {
    return createFormDefinition(unauthenticated(dto));
  }

  public ResponseEntity<FormDefinitionDTO> getFormDefinition(Integer id) {
    return getFormDefinition(id, ADMIN_ROLE);
  }

  public ResponseEntity<FormDefinitionDTO> getFormDefinition(Integer id, String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getFormDefinition(id, entity);
  }

  public ResponseEntity<FormDefinitionDTO> getFormDefinitionUnauthenticated(Integer id) {
    var entity = unauthenticated();
    return getFormDefinition(id, entity);
  }

  public ResponseEntity<FormDefinitionDTO> updateFormDefinition(
      Integer id, FormDefinitionUpdateDTO dto) {
    return updateFormDefinition(id, dto, ADMIN_ROLE);
  }

  public ResponseEntity<FormDefinitionDTO> updateFormDefinition(
      Integer id, FormDefinitionUpdateDTO dto, String role) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return updateFormDefinition(id, entity);
  }

  public ResponseEntity<FormDefinitionDTO> updateFormDefinitionUnauthenticated(
      Integer id, FormDefinitionUpdateDTO dto) {
    var entity = unauthenticated(dto);
    return updateFormDefinition(id, entity);
  }

  public ResponseEntity<FormDefinitionDTO> publishFormDefinition(
      Integer id, FormDefinitionPublishDTO dto) {
    return publishFormDefinition(id, dto, ADMIN_ROLE);
  }

  public ResponseEntity<FormDefinitionDTO> publishFormDefinition(
      Integer id, FormDefinitionPublishDTO dto, String role) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return publishFormDefinition(id, entity);
  }

  public ResponseEntity<FormDefinitionDTO> publishFormDefinitionUnauthenticated(
      Integer id, FormDefinitionPublishDTO dto) {
    var entity = unauthenticated(dto);
    return publishFormDefinition(id, entity);
  }

  public ResponseEntity<RequestFormDTO> createRequestForm(
      Integer requestId, RequestFormCreateDTO dto) {
    var url = String.format("%s/forms", requestUrl(requestId));
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return exchange(url, POST, entity, RequestFormDTO.class);
  }

  public ResponseEntity<RequestFormDTO> getRequestForm(Integer requestId, Integer formId) {
    var url = requestFormUrl(requestId, formId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return exchange(url, GET, entity, RequestFormDTO.class);
  }

  public ResponseEntity<RequestFormDTO> updateRequestForm(
      Integer requestId, Integer formId, RequestFormUpdateDTO dto) {
    var url = requestFormUrl(requestId, formId);
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return exchange(url, PUT, entity, RequestFormDTO.class);
  }

  public ResponseEntity<RequestFormDTO> submitRequestForm(
      Integer requestId, Integer formId, RequestFormSubmitDTO dto) {
    var url = String.format("%s/submissions", requestFormUrl(requestId, formId));
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, ADMIN_ROLE, NO_ACCOUNT_USER_ID1);
    return exchange(url, POST, entity, RequestFormDTO.class);
  }

  public ResponseEntity<PagedResource<RequestFormDTO>> getRequestFormsPage(
      Integer requestId, Integer displayCategoryId) {
    var url = addDisplayCategoryIdFilter(requestFormPagesUrl(requestId), displayCategoryId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestFormsPage(url, entity);
  }

  public ResponseEntity<PagedResource<RequestFormDTO>> getRequestFormsPage(Integer requestId) {
    var url = requestFormPagesUrl(requestId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestFormsPage(url, entity);
  }

  public ResponseEntity<List<RequestFormDTO>> getRequestForms(
      Integer requestId, Integer displayCategoryId) {
    var url = addDisplayCategoryIdFilter(requestFormsUrl(requestId), displayCategoryId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestForms(url, entity);
  }

  public ResponseEntity<List<RequestFormDTO>> getRequestForms(Integer requestId) {
    var url = requestFormsUrl(requestId);
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    return getRequestForms(url, entity);
  }

  private ResponseEntity<PagedResource<RequestFormDTO>> getRequestFormsPage(
      String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestFormDTO.class);
  }

  private ResponseEntity<List<RequestFormDTO>> getRequestForms(String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, RequestFormDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> createFormDefinition(HttpEntity<?> entity) {
    var url = formDefinitionsUrl();
    var response = exchange(url, POST, entity, String.class);
    return toResponse(response, FormDefinitionDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> getFormDefinition(Integer id, HttpEntity<?> entity) {
    var url = formDefinitionUrl(id);
    var response = exchange(url, GET, entity, String.class);
    return toResponse(response, FormDefinitionDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> updateFormDefinition(Integer id, HttpEntity<?> entity) {
    var url = formDefinitionUrl(id);
    return exchange(url, PUT, entity, FormDefinitionDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> publishFormDefinition(
      Integer id, HttpEntity<?> entity) {
    var url = publishFormDefinitionUrl(id);
    return exchange(url, POST, entity, FormDefinitionDTO.class);
  }

  private ResponseEntity<byte[]> downloadDocument(HttpEntity<?> entity, UUID documentId) {
    var url = buildDownloadDocumentUrl(documentId);
    return getFile(url, entity);
  }

  private ResponseEntity<byte[]> getFile(String url, HttpEntity<?> entity) {
    return restClient
        .get()
        .uri(url)
        .headers((headers) -> headers.addAll(entity.getHeaders()))
        .retrieve()
        .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
        .toEntity(byte[].class);
  }

  private ResponseEntity<Document> updateDocument(HttpEntity<?> entity, UUID documentId) {
    var url = String.format("%s/documents/%s", baseUrl, documentId.toString());
    return exchange(url, PUT, entity, Document.class);
  }

  private ResponseEntity<byte[]> getPublicDocument(HttpEntity<?> entity, UUID documentId) {
    var url = buildPublicDownloadDocumentUrl(documentId);
    return getFile(url, entity);
  }

  private ResponseEntity<RequestDTO> getRequestByIdResponse(int id, HttpEntity<?> entity) {
    var url = String.format("%s/%d", requestsUrl(), id);
    return exchange(url, GET, entity, RequestDTO.class);
  }

  private ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequests(
      String url, HttpEntity<?> request) {
    var response = exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, AssignedRequestDTO.class);
  }

  private ResponseEntity<PagedResource<CommonDashboardRequestDTO>> getCommonDashboardRequests(
      String url, HttpEntity<?> request) {
    var response = exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, CommonDashboardRequestDTO.class);
  }

  private ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests(HttpEntity<?> request) {
    return getAllRequests(allRequestsUrl(), request);
  }

  private ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests(
      String url, HttpEntity<?> request) {
    var response = exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, AllRequestsDTO.class);
  }

  private ResponseEntity<PagedResource<RequestDTO>> getRequestsForRole(HttpEntity<?> entity) {
    var url = requestsUrl();
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestDTO.class);
  }

  private ResponseEntity<List<UserDTO>> getUsers(String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, UserDTO.class);
  }

  private ResponseEntity<PagedResource<UserDTO>> getUserPage(String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, UserDTO.class);
  }

  private ResponseEntity<String> getUser(UUID idpUserId, HttpEntity<?> entity) {
    var url = getUserUrl(idpUserId);
    return exchange(url, GET, entity, String.class);
  }

  private ResponseEntity<List<ExtendedReferenceDataType>> getReferenceDataTypes(
      String url, HttpEntity<?> entity) {
    var response = exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, ExtendedReferenceDataType.class);
  }

  private ResponseEntity<UUID> uploadDocument(
      HttpHeaders headers,
      String filePath,
      String uploadName,
      Integer projectId,
      Integer requestId,
      Integer documentTypeId) {
    var entity = getMultipartHttpEntity(filePath, uploadName, headers);
    var requestUrl = projectRequestUrl(projectId, requestId);
    var url = String.format("%s/document-types/%d/documents", requestUrl, documentTypeId);
    var response = exchange(url, POST, entity, String.class);
    return toUUIDResponse(response);
  }

  private ResponseEntity<PagedResource<DocumentDTO>> getRequestDocuments(
      HttpEntity<?> entity, Integer projectId, Integer requestId) {
    var url = requestDocumentsUrl(projectId, requestId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, DocumentDTO.class);
  }

  private ResponseEntity<PagedResource<DocumentDTO>> getRequestDocumentsByDisplayCategoryId(
      HttpEntity<?> entity, Integer projectId, Integer requestId, Integer displayCategoryId) {
    var url = requestDocumentsByDisplayCategoryIdUrl(projectId, requestId, displayCategoryId);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, DocumentDTO.class);
  }

  private ResponseEntity<RequestAccountAssignmentDTO> updateAssignment(
      HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d", baseUrl, id);
    return exchange(url, PUT, entity, RequestAccountAssignmentDTO.class);
  }

  private ResponseEntity<String> updateAssignmentStatus(HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d/status", baseUrl, id);
    return exchange(url, PUT, entity, String.class);
  }

  private ResponseEntity<PagedResource<RequestAccountAssignmentDTO>> getAssignments(
      HttpEntity<?> entity, Integer accountId, String query) {
    var url = String.format("%s/assignments/account/%d%s", baseUrl, accountId, query);
    var response = exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestAccountAssignmentDTO.class);
  }

  private ResponseEntity<RequestAccountAssignmentDTO> getAssignment(
      HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d", baseUrl, id);
    return exchange(url, GET, entity, RequestAccountAssignmentDTO.class);
  }

  private <T> ResponseEntity<PagedResource<T>> getPagedResponse(
      String url, HttpEntity<?> entity, Class<T> responseItemType) {
    var response =
        restClient
            .get()
            .uri(url)
            .headers(headers -> headers.addAll(entity.getHeaders()))
            .retrieve()
            .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
            .toEntity(String.class);
    return toPagedResourceResponse(response, responseItemType);
  }

  private String statusReportUrl(String startDate, String endDate) {
    var queryString = toQueryString(dateRangeToMap(startDate, endDate));
    return String.format("%s/status-report%s", baseUrl, queryString);
  }

  private String statusReportExcelUrl(String startDate, String endDate) {
    var queryString = toQueryString(dateRangeToMap(startDate, endDate));
    return String.format("%s/status-report/excel%s", baseUrl, queryString);
  }

  private Map<String, String> dateRangeToMap(String startDate, String endDate) {
    var map = new HashMap<String, String>();
    Optional.ofNullable(startDate).ifPresent(s -> map.put("startDate", s));
    Optional.ofNullable(endDate).ifPresent(e -> map.put("endDate", e));
    return Collections.unmodifiableMap(map);
  }

  private String projectStatusUrl(Integer projectId) {
    return String.format("%s/%d/status", projectsUrl(), projectId);
  }

  private String projectRiskSummariesUrl(Integer projectId) {
    return String.format("%s/risk-summaries", projectUrl(projectId));
  }

  private String projectUrl(Integer id) {
    return String.format("%s/%d", projectsUrl(), id);
  }

  private String projectsUrl() {
    return String.format("%s/projects", baseUrl);
  }

  private String actuatorUrl(String endpoint) {
    return String.format("%s/actuator/%s", baseUrl, endpoint);
  }

  private String assignedRequestProjectsUrl() {
    return String.format("%s/projects", assignedRequestsUrl());
  }

  private String assignedRequestStatusesUrl() {
    return String.format("%s/request-statuses", assignedRequestsUrl());
  }

  private String assignedRequestTypesUrl() {
    return String.format("%s/request-types", assignedRequestsUrl());
  }

  private String assignedRequestInvoiceStatusesUrl() {
    return String.format("%s/invoice-statuses", assignedRequestsUrl());
  }

  private String assignedRequestAccountsUrl(int accountTypeId) {
    return String.format("%s/accounts?accountTypeId=%d", assignedRequestsUrl(), accountTypeId);
  }

  private String assignedRequestsUrl() {
    return String.format("%s/assigned-requests", baseUrl);
  }

  private String allRequestsUrl() {
    return String.format("%s/all-requests", baseUrl);
  }

  private String batchRequestsUrl() {
    return String.format("%s/batch-requests", baseUrl);
  }

  private String requestFormPagesUrl(Integer requestId) {
    return String.format("%s/pages", requestFormsUrl(requestId));
  }

  private String requestFormUrl(Integer requestId, Integer formId) {
    return String.format("%s/%d", requestFormsUrl(requestId), formId);
  }

  private String requestFormsUrl(Integer requestId) {
    return String.format("%s/forms", requestUrl(requestId));
  }

  private String publishFormDefinitionUrl(Integer id) {
    return String.format("%s/publications", formDefinitionUrl(id));
  }

  private String formDefinitionUrl(Integer id) {
    return String.format("%s/%d", formDefinitionsUrl(), id);
  }

  private String formDefinitionsUrl() {
    return String.format("%s/form-definitions", baseUrl);
  }

  private String requestUrl(Integer requestId) {
    return String.format("%s/requests/%d", baseUrl, requestId);
  }

  private String requestsUrl() {
    return String.format("%s/requests", baseUrl);
  }

  private String addDisplayCategoryIdFilter(String url, Integer displayCategoryId) {
    return String.format("%s?displayCategoryId=%d", url, displayCategoryId);
  }

  private String projectRequestsUrl(int projectId) {
    return String.format("%s/projects/%d/requests", baseUrl, projectId);
  }

  private String getActiveUsersUrl() {
    return String.format("%s?active=true", getUsersUrl());
  }

  private String searchUsersUrl(String searchTerm) {
    return String.format("%s?searchTerm=%s", getUsersUrl(), searchTerm);
  }

  private String userBatchesUrl() {
    return String.format("%s/batches", getUsersUrl());
  }

  private String getUsersUrl() {
    return String.format("%s/users", baseUrl);
  }

  private String getUserPageUrl() {
    return String.format("%s/users/pages", baseUrl);
  }

  private String getUserUrl(UUID idpUserId) {
    return String.format("%s?idpUserId=%s", getUserUrl(), idpUserId);
  }

  private String getUserUrl() {
    return String.format("%s/user", baseUrl);
  }

  private String invoicesUrl(Integer invoiceId) {
    return String.format("%s/%d", invoicesUrl(), invoiceId);
  }

  private String invoicesUrl() {
    return String.format("%s/invoices", baseUrl);
  }

  private String requestDocumentsUrl(Integer projectId, Integer requestId) {
    var requestUrl = projectRequestUrl(projectId, requestId);
    return String.format("%s/documents", requestUrl);
  }

  private String requestDocumentsByDisplayCategoryIdUrl(
      Integer projectId, Integer requestId, Integer displayCategoryId) {
    var requestUrl = projectRequestUrl(projectId, requestId);
    return String.format("%s/documents/%d", requestUrl, displayCategoryId);
  }

  private String buildPublicDownloadDocumentUrl(UUID documentId) {
    return String.format("%s/public/documents/%s/download", baseUrl, documentId);
  }

  private String buildDownloadDocumentUrl(UUID documentId) {
    return String.format("%s/documents/%s/download", baseUrl, documentId);
  }

  private String getAssignmentSummariesUrl() {
    return String.format("%s/assignment-summaries", baseUrl);
  }

  private String projectRequestUrl(int projectId, int requestId) {
    return String.format("%s/projects/%d/requests/%d", baseUrl, projectId, requestId);
  }

  private String conversationsUrl(int projectId, int requestId) {
    return String.format("%s/conversations", projectRequestUrl(projectId, requestId));
  }

  private String conversationMessagePagesUrl(int projectId, int requestId, int conversationId) {
    var messagesUrl = conversationMessagesUrl(projectId, requestId, conversationId);
    return String.format("%s/pages", messagesUrl);
  }

  private String conversationMessageReadsUrl(int projectId, int requestId, int conversationId) {
    var messagesUrl = conversationMessagesUrl(projectId, requestId, conversationId);
    return String.format("%s/reads", messagesUrl);
  }

  private String conversationMessagesUrl(int projectId, int requestId, int conversationId) {
    return String.format("%s/%d/messages", conversationsUrl(projectId, requestId), conversationId);
  }

  private String mfaBaseUrl() {
    return String.format("%s/mfa", baseUrl);
  }

  private String mfaSettingsUrl() {
    return String.format("%s/settings", mfaBaseUrl());
  }

  private String mfaSoftwareTokenSetupUrl() {
    return String.format("%s/software-token/setup", mfaBaseUrl());
  }

  private String mfaSoftwareTokenVerifyUrl() {
    return String.format("%s/software-token/verify", mfaBaseUrl());
  }

  private String accountCompetenciesUrl(Integer accountId) {
    return String.format("%s/competencies", accountUrl(accountId));
  }

  private String accountUrl(Integer id) {
    return String.format("%s/%d", accountsUrl(), id);
  }

  private String accountsUrl() {
    return String.format("%s/accounts", baseUrl);
  }

  private String toConsultationsUrl(int requestId) {
    return String.format("%s/requests/%d/live-stakeholder-consultations", baseUrl, requestId);
  }

  private String toPagedConsultationsUrl(ZonedDateTime now, Integer typeId) {
    String typeParam =
        Optional.ofNullable(typeId)
            .map(id -> String.format("&consultationTypeId=%d", id))
            .orElse("");
    return String.format("%s/stakeholder-consultations/pages?liveAt=%s%s", baseUrl, now, typeParam);
  }

  private String toConsultationCompletionUrl(int requestId) {
    return String.format("%s/completions", toConsultationsUrl(requestId));
  }

  private String registryUpdateUrl() {
    return String.format("%s/registry-updates/projects", baseUrl);
  }

  private HttpEntity<?> unauthenticated(Object body) {
    return new HttpEntity<>(body, new HttpHeaders());
  }

  private HttpEntity<?> unauthenticated() {
    return new HttpEntity<>(new HttpHeaders());
  }

  private HttpEntity<?> getArmUserRequestEntity() {
    return getHttpEntityWithTokenForRoleWithId(ARM_TEAM_ROLE, ARM_USER_ID);
  }

  private HttpEntity<?> getHttpEntityWithTokenForRole(String role) {
    return new HttpEntity<>(getHttpHeadersWithToken(role, null));
  }

  private HttpEntity<?> getHttpEntityWithTokenForRoleWithId(String role, UUID id) {
    return new HttpEntity<>(getHttpHeadersWithToken(role, id));
  }

  private <T> HttpEntity<String> getHttpEntityWithTokenAndBodyForRole(T body, String role) {
    return getHttpEntityWithTokenAndBodyForRoleWithId(body, role, null);
  }

  private <T> HttpEntity<String> getHttpEntityWithTokenAndBodyForRoleWithId(
      T body, String role, UUID id) {
    var headers = getHttpHeadersWithToken(role, id);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return new HttpEntity<>(toJson(body), headers);
  }

  private HttpEntity<MultiValueMap<String, Object>> getHttpEntityWithTokenAndFormBodyForRoleWithId(
      MultiValueMap<String, Object> body, String role, UUID id) {
    var headers = getHttpHeadersWithToken(role, id);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    return new HttpEntity<>(body, headers);
  }

  private HttpEntity<?> getMultipartHttpEntity(
      String filePath, String uploadName, HttpHeaders headers) {
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    Resource file = new ClassPathResource(filePath);
    builder.part("file", file, MediaType.TEXT_PLAIN).filename(uploadName);
    return new HttpEntity<>(builder.build(), headers);
  }

  private HttpEntity<?> getHttpEntityWithToken() {
    return new HttpEntity<>(getHttpHeadersWithToken(null, null));
  }

  private HttpHeaders getHttpHeadersWithToken(String role, UUID id) {
    var token = getToken(role, id);
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.COOKIE, buildAuthCookie(token).toString());
    return headers;
  }

  private ResponseCookie buildAuthCookie(String accessToken) {
    return ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        .secure(true)
        .path("/api")
        .maxAge(Duration.ofDays(1))
        .build();
  }

  private String getToken(String role, UUID id) {
    var url = String.format("%s/token%s", baseUrl, toQueryString(role, id));
    return restClient.get().uri(url).retrieve().body(String.class);
  }

  private <T> ResponseEntity<List<T>> toParameterizedListResponse(
      ResponseEntity<String> original, Class<?> itemType) {
    Optional<ResponseEntity<List<T>>> response = toNullBodyResponseIfNotSuccessful(original);
    var type = toParametricType(List.class, itemType);
    return response.orElseGet(
        () -> copyWithoutBody(original).body(toObject(original.getBody(), type)));
  }

  private <T> ResponseEntity<PagedResource<T>> toPagedResourceResponse(
      ResponseEntity<String> original, Class<?> itemType) {
    Optional<ResponseEntity<PagedResource<T>>> response =
        toNullBodyResponseIfNotSuccessful(original);
    var type = toParametricType(PagedResource.class, itemType);
    return response.orElseGet(
        () -> copyWithoutBody(original).body(toObject(original.getBody(), type)));
  }

  private JavaType toParametricType(Class<?> parametrizedType, Class<?> itemType) {
    return objectMapper.getTypeFactory().constructParametricType(parametrizedType, itemType);
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JacksonException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private <T> T toObject(String content, JavaType valueType) {
    try {
      return objectMapper.readValue(Objects.requireNonNull(content), valueType);
    } catch (JacksonException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private <T> ResponseEntity<T> exchange(
      String url, HttpMethod method, HttpEntity<?> entity, Class<T> responseType) {
    var requestSpec =
        restClient
            .method(method)
            .uri(url)
            .headers((headers) -> headers.addAll(entity.getHeaders()));
    if (Objects.nonNull(entity.getBody())) {
      requestSpec = requestSpec.body(entity.getBody());
    }
    var response =
        requestSpec
            .retrieve()
            .onStatus(ApiClient::isNotSuccessful, ApiClient::swallowException)
            .toEntity(String.class);
    return toResponse(response, responseType);
  }

  private <T> ResponseEntity<T> toResponse(ResponseEntity<String> original, Class<T> type) {
    Optional<ResponseEntity<T>> response = toNullBodyResponseIfNotSuccessful(original);
    return response.orElseGet(
        () -> copyWithoutBody(original).body(unwrapResponseBody(original.getBody(), type)));
  }

  private <T> T unwrapResponseBody(String body, Class<T> type) {
    if (Objects.isNull(body)) {
      return null;
    }
    if (type.equals(String.class)) {
      return type.cast(unwrap(body));
    }
    try {
      return objectMapper.readValue(unwrap(body), type);
    } catch (JacksonException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static ResponseEntity<Integer> toIntegerResponse(ResponseEntity<String> original) {
    Optional<ResponseEntity<Integer>> response = toNullBodyResponseIfNotSuccessful(original);
    return response.orElseGet(
        () ->
            copyWithoutBody(original)
                .body(Integer.parseInt(Objects.requireNonNull(original.getBody()))));
  }

  private static ResponseEntity<UUID> toUUIDResponse(ResponseEntity<String> original) {
    Optional<ResponseEntity<UUID>> response = toNullBodyResponseIfNotSuccessful(original);
    return response.orElseGet(
        () -> copyWithoutBody(original).body(unwrapUUIDResponseBody(original.getBody())));
  }

  private static UUID unwrapUUIDResponseBody(String body) {
    return UUID.fromString(unwrap(body));
  }

  private static String unwrap(String escapedJson) {
    return StringUtils.unwrap(Objects.requireNonNull(escapedJson), "\"");
  }

  private static <T> Optional<ResponseEntity<T>> toNullBodyResponseIfNotSuccessful(
      ResponseEntity<String> original) {
    if (!original.getStatusCode().is2xxSuccessful()) {
      return Optional.of(copyWithoutBody(original).body(null));
    }
    return Optional.empty();
  }

  private static ResponseEntity.BodyBuilder copyWithoutBody(ResponseEntity<String> original) {
    return ResponseEntity.status(original.getStatusCode()).headers(original.getHeaders());
  }

  private static String toBaseUrl(int port) {
    return String.format("http://localhost:%d", port);
  }

  private static String toQueryString(String role, UUID id) {
    var map = new HashMap<String, String>();
    Optional.ofNullable(role).ifPresent(r -> map.put("role", r));
    Optional.ofNullable(id).ifPresent(i -> map.put("id", i.toString()));
    return toQueryString(map);
  }

  private static String toQueryString(Map<String, String> params) {
    Collection<String> query = new ArrayList<>();
    for (var entry : params.entrySet()) {
      if (Objects.nonNull(entry.getValue())) {
        query.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
      }
    }
    if (query.isEmpty()) {
      return "";
    }
    return String.format("?%s", String.join("&", query));
  }

  private static boolean isNotSuccessful(HttpStatusCode statusCode) {
    return !statusCode.is2xxSuccessful();
  }

  private static void swallowException(HttpRequest request, ClientHttpResponse response)
      throws IOException {
    log.info(
        "swallowing exception for {} {} http response {}",
        request.getMethod(),
        request.getURI(),
        response.getStatusCode());
  }
}
