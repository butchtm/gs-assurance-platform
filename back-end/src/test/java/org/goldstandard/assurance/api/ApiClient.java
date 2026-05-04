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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import org.goldstandard.assurance.api.dto.ConversationDTO;
import org.goldstandard.assurance.api.dto.ConversationMessageDTO;
import org.goldstandard.assurance.api.dto.DocumentDTO;
import org.goldstandard.assurance.api.dto.DocumentUpdate;
import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
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
import org.goldstandard.assurance.api.entity.lookup.BatchType;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;

@Lazy // allows for initialization after the server port is known
@Component
@RequiredArgsConstructor
public class ApiClient {

  private final String baseUrl;
  private final ObjectMapper objectMapper;
  private final TestRestTemplate restTemplate;

  @Autowired
  public ApiClient(
      @LocalServerPort int port, ObjectMapper objectMapper, TestRestTemplate restTemplate) {
    this(toBaseUrl(port), objectMapper, restTemplate);
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
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ProjectDTO.class);
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
    return exchange(url, POST, entity, Integer.class);
  }

  public ResponseEntity<ProjectDTO> getProject(Integer id, String role) {
    var url = projectUrl(id);
    var entity = getHttpEntityWithTokenForRole(role);
    return getProject(url, entity);
  }

  public ResponseEntity<ProjectDTO> getProject(Integer id, String role, UUID userId) {
    var url = projectUrl(id);
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return getProject(url, entity);
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
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, MethodologyHistoryDTO.class);
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
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ProjectRiskSummaryDTO.class);
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
    return exchange(url, PUT, entity, Integer.class);
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
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<Void> exportCommonDashboardReport(String query) {
    return exportCommonDashboardReport(ARM_TEAM_ROLE, ARM_USER_ID, query);
  }

  public ResponseEntity<Void> exportCommonDashboardReport(String role, UUID userId, String query) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/common-dashboard-requests/reports?%s", baseUrl, query);
    return restTemplate.exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<StatusReportDTO> getStatusReport(String role) {
    return getStatusReport(role, null, null);
  }

  public ResponseEntity<StatusReportDTO> getStatusReport(
      String role, String startDate, String endDate) {
    var entity = getHttpEntityWithTokenForRole(role);
    var urlBuilder = new StringBuilder(String.format("%s/status-report", baseUrl));

    var params = new ArrayList<String>();
    if (startDate != null) {
      params.add("startDate=" + startDate);
    }
    if (endDate != null) {
      params.add("endDate=" + endDate);
    }

    if (!params.isEmpty()) {
      urlBuilder.append("?").append(String.join("&", params));
    }

    return restTemplate.exchange(urlBuilder.toString(), GET, entity, StatusReportDTO.class);
  }

  public ResponseEntity<byte[]> getStatusReportExcel(String role) {
    return getStatusReportExcel(role, null, null);
  }

  public ResponseEntity<byte[]> getStatusReportExcel(
      String role, String startDate, String endDate) {
    var entity = getHttpEntityWithTokenForRole(role);
    var urlBuilder = new StringBuilder(String.format("%s/status-report/excel", baseUrl));

    var params = new ArrayList<String>();
    if (startDate != null) {
      params.add("startDate=" + startDate);
    }
    if (endDate != null) {
      params.add("endDate=" + endDate);
    }

    if (!params.isEmpty()) {
      urlBuilder.append("?").append(String.join("&", params));
    }

    return restTemplate.exchange(urlBuilder.toString(), GET, entity, byte[].class);
  }

  public ResponseEntity<List<Integer>> updateProjectStatus(
      String role, Integer projectId, ProjectStatusUpdateDTO update) {
    var entity = getHttpEntityWithTokenAndBodyForRole(update, role);
    var url = projectStatusUrl(projectId);
    return restTemplate.exchange(url, PUT, entity, new ParameterizedTypeReference<>() {});
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
    return restTemplate.exchange(url, GET, entity, InvoiceDTO.class);
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
    return restTemplate.exchange(url, POST, entity, InvoiceDTO.class);
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
    return restTemplate.exchange(url, PATCH, entity, InvoiceDTO.class);
  }

  public ResponseEntity<Integer> createBatchRequest(BatchRequestCreateDTO createDTO) {
    var entity = getHttpEntityWithTokenAndBodyForRole(createDTO, ADMIN_ROLE);
    var url = batchRequestsUrl();
    return restTemplate.exchange(url, POST, entity, Integer.class);
  }

  public ResponseEntity<List<BatchType>> getBatchTypes(GetBatchTypesDTO getDTO) {
    var entity = this.getHttpEntityWithTokenAndBodyForRole(getDTO, ADMIN_ROLE);
    var url = String.format("%s/types", batchRequestsUrl());
    return restTemplate.exchange(url, POST, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<BatchProjectsDTO> getPublicBatchProjects(Integer batchId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/public/batch/%d/projects", baseUrl, batchId);
    return this.restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<BatchProjectsDTO> getBatchProjects(Integer batchId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/%d/projects", batchRequestsUrl(), batchId);
    return this.restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsByProjectId(Integer projectId) {
    var entity = getHttpEntityWithTokenForRole(ARM_TEAM_ROLE);
    var url = String.format("%s?projectId=%d", requestsUrl(), projectId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<ProjectDeveloperRequestDTO>> getProjectDeveloperRequests(
      GetProjectDeveloperRequestsDTO dto) {
    var entity =
        getHttpEntityWithTokenAndBodyForRoleWithId(
            dto, PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);
    var url = String.format("%s/project-developer-requests/pages", baseUrl);
    return restTemplate.exchange(url, POST, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<TacRequestDTO>> getTacRequests() {
    var entity = getHttpEntityWithTokenForRole(TAC_ROLE);
    var url = String.format("%s/requests/consultation", baseUrl);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<String> getPublicConsultations() {
    var entity = unauthenticated();
    var url = String.format("%s/public/consultations/pages", baseUrl);
    return restTemplate.exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<String> getPublicConsultation(String gsid) {
    var entity = unauthenticated();
    var url = String.format("%s/public/consultations/%s", baseUrl, gsid);
    return restTemplate.exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<Integer> publicCreateComment(CommentCreateDTO dto) {
    var entity = unauthenticated(dto);
    var url = String.format("%s/public/comments", baseUrl);
    return restTemplate.exchange(url, POST, entity, Integer.class);
  }

  public ResponseEntity<String> publicGetRequestProjectGsId(int id) {
    var entity = unauthenticated();
    var url = String.format("%s/public/requests/%d/gsid", baseUrl, id);
    return restTemplate.exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<List<ProjectDTO>> getProjectDeveloperRequestProjects() {
    var entity =
        getHttpEntityWithTokenForRoleWithId(PROJECT_DEVELOPER_ROLE, PROJECT_DEVELOPER_USER_ID);
    var url = String.format("%s/project-developer-requests/projects", baseUrl);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsForRole(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    return getRequestsForRole(entity);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsForUser(String role, UUID idpUserId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, idpUserId);
    return getRequestsForRole(entity);
  }

  public ResponseEntity<PagedResource<RequestDTO>> getRequestsWithoutRole() {
    return getRequestsForRole(getHttpEntityWithToken());
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
    return restTemplate.exchange(projectRequestsUrl(projectId), POST, entity, Integer.class);
  }

  public ResponseEntity<Integer> updateRequest(
      int projectId, RequestCreateUpdateDTO dto, String role, UUID idpUserId) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, idpUserId);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> updateRequest(int projectId, RequestCreateUpdateDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> updateRequest(
      int projectId, RequestCreateUpdateDTO dto, Boolean isArmUser) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, ARM_TEAM_ROLE, ARM_USER_ID);
    return updateRequest(projectId, entity);
  }

  public ResponseEntity<Integer> updateRequestUnauthenticated(
      int projectId, RequestCreateUpdateDTO dto) {
    return updateRequest(projectId, unauthenticated(dto));
  }

  public ResponseEntity<Integer> updateRequest(int projectId, HttpEntity<?> entity) {
    var response = restTemplate.exchange(projectRequestsUrl(projectId), PUT, entity, String.class);
    return toIntegerResponse(response);
  }

  public ResponseEntity<Map<String, List<RequestTypeDocumentTypeDTO>>> getRequiredDocumentTypes(
      int requestTypeId) {
    var entity = getHttpEntityWithToken();
    var url = String.format("%s/requests/required-document-types/%d", baseUrl, requestTypeId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
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
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
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
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<ProblemDetail> getAssignedRequestAccountsError(int accountTypeId) {
    var url = assignedRequestAccountsUrl(accountTypeId);
    var entity = getArmUserRequestEntity();
    return restTemplate.exchange(url, GET, entity, ProblemDetail.class);
  }

  public ResponseEntity<PagedResource<BundleProjectDTO>> getBundleProjects(Integer bundleId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/bundles/%d/projects/pages", baseUrl, bundleId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<List<Project>> getCandidatePOAs(
      String role, GetCandidatePOAsRequest request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/projects/candidate-poas", baseUrl);
    return restTemplate.exchange(url, POST, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<List<Project>> getCandidateRealCaseVPAs(
      String role, GetCandidateRealCaseVPAsRequest request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/projects/candidate-real-case-vpas", baseUrl);
    return restTemplate.exchange(url, POST, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<PagedResource<RequestActivityDTO>> getRequestActivityHistory(
      Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/requests/%d/activities/pages", baseUrl, requestId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<List<PathwayGroupTimelineActivityDTO>> getRequestPathwayGroupTimeline(
      Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/requests/%d/current-pathway-group-timelines", baseUrl, requestId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<Void> closeActivity(Integer requestActivityId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/request-activities/%d/completions", baseUrl, requestActivityId);
    return restTemplate.exchange(url, POST, entity, Void.class);
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
    return restTemplate.exchange(url, GET, entity, String.class);
  }

  public ResponseEntity<byte[]> getPublicDocument(UUID documentId) {
    var entity = unauthenticated();
    return getPublicDocument(entity, documentId);
  }

  private ResponseEntity<byte[]> getPublicDocument(HttpEntity<?> entity, UUID documentId) {
    var url = buildPublicDownloadDocumentUrl(documentId);
    return downloadDocument(url, entity);
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
    return restTemplate.exchange(url, DELETE, entity, Void.class);
  }

  public ResponseEntity<PagedResource<ProjectTreeNodeDTO>> getProjectTrees(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/projects/trees/pages", baseUrl);
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, ProjectTreeNodeDTO.class);
  }

  public ResponseEntity<ProjectTreeNodeDTO> getProjectTree(String role, Integer projectId) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/projects/%d/trees", baseUrl, projectId);
    return restTemplate.exchange(url, GET, entity, ProjectTreeNodeDTO.class);
  }

  public ResponseEntity<PagedResource<MethodologyManagementDTO>> getAllMethodologiesPagedAsAdmin() {
    return getAllMethodologiesPagedAsAdmin("");
  }

  public ResponseEntity<PagedResource<MethodologyManagementDTO>> getAllMethodologiesPagedAsAdmin(
      String description) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/methodologies/pages?description=%s", baseUrl, description);
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, MethodologyManagementDTO.class);
  }

  public ResponseEntity<List<Methodology>> getAllMethodologiesAs(String role) {
    var entity = getHttpEntityWithTokenForRole(role);
    var url = String.format("%s/methodologies", baseUrl);
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, Methodology.class);
  }

  public ResponseEntity<MethodologyManagementDTO> createMethodologyAsAdmin(
      MethodologyManagementDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    var url = String.format("%s/methodologies", baseUrl);
    return restTemplate.exchange(url, POST, entity, MethodologyManagementDTO.class);
  }

  public ResponseEntity<MethodologyManagementDTO> updateMethodologyAsAdmin(
      MethodologyManagementDTO dto, Integer id) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, ADMIN_ROLE);
    var url = String.format("%s/methodologies/%s", baseUrl, id);
    return restTemplate.exchange(url, PUT, entity, MethodologyManagementDTO.class);
  }

  public ResponseEntity<Void> deleteMethodologyAsAdmin(Integer id) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/methodologies/%s", baseUrl, id);
    return restTemplate.exchange(url, DELETE, entity, Void.class);
  }

  public ResponseEntity<List<CommentDTO>> getRequestComments(Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s/comments/%d", baseUrl, requestId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
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
    return restTemplate.exchange(url, GET, entity, Boolean.class);
  }

  public ResponseEntity<TextAnalysisResultsDTO> analyseText(String role, TextAnalysisDTO request) {
    var entity = getHttpEntityWithTokenAndBodyForRole(request, role);
    var url = String.format("%s/pii-analysis", baseUrl);
    return restTemplate.exchange(url, POST, entity, TextAnalysisResultsDTO.class);
  }

  public ResponseEntity<TextAnalysisResultsDTO> publicAnalyseText(TextAnalysisDTO request) {
    var entity = unauthenticated(request);
    var url = String.format("%s/public/pii-analysis", baseUrl);
    return restTemplate.exchange(url, POST, entity, TextAnalysisResultsDTO.class);
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
    return restTemplate.exchange(url, POST, entity, Void.class);
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
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<ConversationDTO> openConversation(int projectId, int requestId) {
    var entity = getArmUserRequestEntity();
    var url = conversationsUrl(projectId, requestId);
    return restTemplate.exchange(url, POST, entity, ConversationDTO.class);
  }

  public ResponseEntity<ConversationDTO> updateConversation(
      int projectId, int requestId, int conversationId, ConversationStatus status) {
    var url = String.format("%s/%d/status", conversationsUrl(projectId, requestId), conversationId);
    var entity = getHttpEntityWithTokenAndBodyForRole(status, ARM_TEAM_ROLE);
    return restTemplate.exchange(url, PUT, entity, ConversationDTO.class);
  }

  public ResponseEntity<ConversationMessageDTO> sendMessage(
      int projectId, int requestId, int conversationId, MessageCreateDTO message) {
    var url = conversationMessagesUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(message, ARM_TEAM_ROLE, ARM_USER_ID);
    return restTemplate.exchange(url, POST, entity, ConversationMessageDTO.class);
  }

  public ResponseEntity<PagedResource<ConversationMessageDTO>> getMessages(
      int projectId, int requestId, int conversationId) {
    var url = conversationMessagePagesUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenForRoleWithId(ARM_TEAM_ROLE, ARM_USER_ID);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<Void> markMessagesAsRead(int projectId, int requestId, int conversationId) {
    var url = conversationMessageReadsUrl(projectId, requestId, conversationId);
    var entity = getHttpEntityWithTokenForRoleWithId(ARM_TEAM_ROLE, ARM_USER_ID);
    return restTemplate.exchange(url, POST, entity, Void.class);
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
    return restTemplate.exchange(url, POST, entity, RequestAccountAssignmentDTO.class);
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
    var response = restTemplate.exchange(url, GET, entity, String.class);
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
    return restTemplate.exchange(url, GET, entity, AssignmentSummaryResponseDTO.class);
  }

  public ResponseEntity<AssignmentSummaryResponseDTO> getReviewAssignmentSummary(
      String filter, String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    var url = String.format("%s/review?%s", getAssignmentSummariesUrl(), filter);
    return restTemplate.exchange(url, GET, entity, AssignmentSummaryResponseDTO.class);
  }

  public ResponseEntity<MfaSettingsDTO> getMfaSettings(String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return restTemplate.exchange(mfaSettingsUrl(), GET, entity, MfaSettingsDTO.class);
  }

  public ResponseEntity<MfaSettingsDTO> getMfaSettingsAsAdmin(String targetUsername) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("%s?username=%s", mfaSettingsUrl(), targetUsername);
    return restTemplate.exchange(url, GET, entity, MfaSettingsDTO.class);
  }

  public ResponseEntity<UpdateMfaSettingsResponseDTO> updateMfaSettings(
      String role, UUID userId, MfaSettingsDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return restTemplate.exchange(
        mfaSettingsUrl(), HttpMethod.PUT, entity, UpdateMfaSettingsResponseDTO.class);
  }

  public ResponseEntity<UpdateMfaSettingsResponseDTO> updateMfaSettings(
      String role, MfaSettingsDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRole(dto, role);
    return restTemplate.exchange(
        mfaSettingsUrl(), HttpMethod.PUT, entity, UpdateMfaSettingsResponseDTO.class);
  }

  public ResponseEntity<SoftwareTokenSetupResponseDTO> setupSoftwareToken(
      String role, UUID userId) {
    var entity = getHttpEntityWithTokenForRoleWithId(role, userId);
    return restTemplate.exchange(
        mfaSoftwareTokenSetupUrl(), POST, entity, SoftwareTokenSetupResponseDTO.class);
  }

  public ResponseEntity<VerifySoftwareTokenResponseDTO> verifySoftwareToken(
      String role, UUID userId, VerifySoftwareTokenRequestDTO dto) {
    var entity = getHttpEntityWithTokenAndBodyForRoleWithId(dto, role, userId);
    return restTemplate.exchange(
        mfaSoftwareTokenVerifyUrl(), POST, entity, VerifySoftwareTokenResponseDTO.class);
  }

  public ResponseEntity<Void> updatePiiToNotFound(
      Integer projectId, Collection<String> projectFields) {
    var entity =
        getHttpEntityWithTokenAndBodyForRoleWithId(projectFields, ARM_TEAM_ROLE, ARM_USER_ID);
    var url = String.format("%s/pii-metadata/%s/action/not-found", baseUrl, projectId);
    return restTemplate.exchange(url, POST, entity, new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<Void> issueRequestSalesOrder(Integer requestId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("/requests/%d/sales-orders", requestId);
    return restTemplate.exchange(url, POST, entity, Void.class);
  }

  public ResponseEntity<Void> issueProjectContract(String gsId) {
    var entity = getHttpEntityWithTokenForRole(ADMIN_ROLE);
    var url = String.format("/projects/%s/contracts", gsId);
    return restTemplate.exchange(url, POST, entity, Void.class);
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
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toPagedResourceResponse(response, RequestFormDTO.class);
  }

  private ResponseEntity<List<RequestFormDTO>> getRequestForms(String url, HttpEntity<?> entity) {
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, RequestFormDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> createFormDefinition(HttpEntity<?> entity) {
    var url = formDefinitionsUrl();
    var response = restTemplate.exchange(url, POST, entity, String.class);
    return toResponse(response, FormDefinitionDTO.class);
  }

  private ResponseEntity<FormDefinitionDTO> getFormDefinition(Integer id, HttpEntity<?> entity) {
    var url = formDefinitionUrl(id);
    var response = restTemplate.exchange(url, GET, entity, String.class);
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
    return downloadDocument(url, entity);
  }

  private ResponseEntity<byte[]> downloadDocument(String url, HttpEntity<?> entity) {
    return restTemplate.exchange(url, GET, entity, byte[].class);
  }

  private ResponseEntity<Document> updateDocument(HttpEntity<?> entity, UUID documentId) {
    var url = String.format("%s/documents/%s", baseUrl, documentId.toString());
    return restTemplate.exchange(url, PUT, entity, Document.class);
  }

  private ResponseEntity<RequestDTO> getRequestByIdResponse(int id, HttpEntity<?> entity) {
    var url = String.format("%s/%d", requestsUrl(), id);
    return restTemplate.exchange(url, GET, entity, RequestDTO.class);
  }

  private ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequests(
      String url, HttpEntity<?> request) {
    var response = restTemplate.exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, AssignedRequestDTO.class);
  }

  private ResponseEntity<PagedResource<CommonDashboardRequestDTO>> getCommonDashboardRequests(
      String url, HttpEntity<?> request) {
    var response = restTemplate.exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, CommonDashboardRequestDTO.class);
  }

  private ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests(HttpEntity<?> request) {
    return getAllRequests(allRequestsUrl(), request);
  }

  private ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests(
      String url, HttpEntity<?> request) {
    var response = restTemplate.exchange(url, GET, request, String.class);
    return toPagedResourceResponse(response, AllRequestsDTO.class);
  }

  private ResponseEntity<PagedResource<RequestDTO>> getRequestsForRole(HttpEntity<?> entity) {
    return restTemplate.exchange(requestsUrl(), GET, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<List<UserDTO>> getUsers(String url, HttpEntity<?> entity) {
    var response = restTemplate.exchange(url, GET, entity, String.class);
    return toParameterizedListResponse(response, UserDTO.class);
  }

  private ResponseEntity<PagedResource<UserDTO>> getUserPage(String url, HttpEntity<?> entity) {
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<String> getUser(UUID idpUserId, HttpEntity<?> entity) {
    var url = getUserUrl(idpUserId);
    return restTemplate.exchange(url, GET, entity, String.class);
  }

  private ResponseEntity<List<ExtendedReferenceDataType>> getReferenceDataTypes(
      String url, HttpEntity<?> entity) {
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
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
    var response = restTemplate.exchange(url, POST, entity, String.class);
    return toUUIDResponse(response);
  }

  private ResponseEntity<PagedResource<DocumentDTO>> getRequestDocuments(
      HttpEntity<?> entity, Integer projectId, Integer requestId) {
    var url = requestDocumentsUrl(projectId, requestId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<PagedResource<DocumentDTO>> getRequestDocumentsByDisplayCategoryId(
      HttpEntity<?> entity, Integer projectId, Integer requestId, Integer displayCategoryId) {
    var url = requestDocumentsByDisplayCategoryIdUrl(projectId, requestId, displayCategoryId);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<RequestAccountAssignmentDTO> updateAssignment(
      HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d", baseUrl, id);
    return restTemplate.exchange(url, PUT, entity, RequestAccountAssignmentDTO.class);
  }

  private ResponseEntity<String> updateAssignmentStatus(HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d/status", baseUrl, id);
    return restTemplate.exchange(url, PUT, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<PagedResource<RequestAccountAssignmentDTO>> getAssignments(
      HttpEntity<?> entity, Integer accountId, String query) {
    var url = String.format("%s/assignments/account/%d%s", baseUrl, accountId, query);
    return restTemplate.exchange(url, GET, entity, new ParameterizedTypeReference<>() {});
  }

  private ResponseEntity<RequestAccountAssignmentDTO> getAssignment(
      HttpEntity<?> entity, Integer id) {
    var url = String.format("%s/assignments/%d", baseUrl, id);
    return restTemplate.exchange(url, GET, entity, RequestAccountAssignmentDTO.class);
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
    return new HttpEntity<>(toJson(body), headers);
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
    var url = String.format("%s/token%s", baseUrl, toQueryParams(role, id));
    return restTemplate.getForObject(url, String.class);
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
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private <T> T toObject(String content, JavaType valueType) {
    try {
      return objectMapper.readValue(Objects.requireNonNull(content), valueType);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private <T> ResponseEntity<T> exchange(
      String url, HttpMethod method, HttpEntity<?> entity, Class<T> responseType) {
    var response = restTemplate.exchange(url, method, entity, String.class);
    return toResponse(response, responseType);
  }

  private <T> ResponseEntity<T> toResponse(ResponseEntity<String> original, Class<T> type) {
    Optional<ResponseEntity<T>> response = toNullBodyResponseIfNotSuccessful(original);
    return response.orElseGet(
        () -> copyWithoutBody(original).body(unwrapResponseBody(original.getBody(), type)));
  }

  private <T> T unwrapResponseBody(String body, Class<T> type) {
    try {
      return objectMapper.readValue(unwrap(body), type);
    } catch (JsonProcessingException e) {
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

  private static String toQueryParams(String role, UUID id) {
    Collection<String> params = new ArrayList<>();
    Optional.ofNullable(role).map(r -> String.format("role=%s", r)).ifPresent(params::add);
    Optional.ofNullable(id).map(i -> String.format("id=%s", i)).ifPresent(params::add);
    if (params.isEmpty()) {
      return "";
    }
    return String.format("?%s", String.join("&", params));
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
}
