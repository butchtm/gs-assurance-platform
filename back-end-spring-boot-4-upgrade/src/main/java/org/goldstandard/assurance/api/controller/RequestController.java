package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.RequestDocumentTypeConverter;
import org.goldstandard.assurance.api.dto.RequestCreateUpdateDTO;
import org.goldstandard.assurance.api.dto.RequestDTO;
import org.goldstandard.assurance.api.dto.RequestTypeDocumentTypeDTO;
import org.goldstandard.assurance.api.dto.TacRequestDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.facade.RequestFacade;
import org.goldstandard.assurance.api.service.RequestTypeDocumentTypeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestController {

  private final RequestTypeDocumentTypeService documentTypeService;
  private final RequestFacade requestFacade;
  private final RequestDocumentTypeConverter requestDocumentTypeConverter;

  @RolesAllowed({ADMIN_ROLE, TAC_ROLE})
  @GetMapping("/requests/consultation")
  public ResponseEntity<PagedResource<TacRequestDTO>> getAllRequestsInConsultation(
      @PageableDefault(
              sort = {"last_request_status_change_date"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(
        new PagedResource<>(requestFacade.getRequestsInConsultation(pageable)));
  }

  @RolesAllowed({ADMIN_ROLE, TAC_ROLE})
  @GetMapping("/requests/requests-in-consultation-by-competencies")
  public ResponseEntity<PagedResource<TacRequestDTO>> getAllRequestsInConsultationByCompetencies(
      @PageableDefault(
              sort = {"last_request_status_change_date"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(
        new PagedResource<>(requestFacade.getRequestsInConsultationByCompetencies(pageable)));
  }

  @GetMapping("/requests")
  public ResponseEntity<PagedResource<RequestDTO>> getRequests(
      @RequestParam(value = "projectId", required = false) Integer projectId,
      @PageableDefault(
              sort = {"id"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    var page = requestFacade.getRequestDetailPage(projectId, pageable);
    return ResponseEntity.ok(new PagedResource<>(page));
  }

  @GetMapping("/requests/{requestId}")
  public ResponseEntity<RequestDTO> getRequest(
      @PathVariable(value = "requestId") Integer requestId) {
    return ResponseEntity.ok().body(requestFacade.getRequestDetailById(requestId));
  }

  @RolesAllowed({ADMIN_ROLE, PROJECT_DEVELOPER_ROLE, ARM_TEAM_ROLE})
  @PostMapping("/projects/{projectId}/requests")
  public ResponseEntity<Integer> createRequest(
      @PathVariable(value = "projectId") Integer projectId,
      @Valid @RequestBody RequestCreateUpdateDTO createRequest) {
    var createdRequest = requestFacade.createAndSaveRequest(projectId, createRequest);
    return ResponseEntity.ok(createdRequest.getId());
  }

  @RolesAllowed({
    ADMIN_ROLE,
    GS_REVIEWER_ROLE,
    GS_REGISTRY_ROLE,
    VVB_ROLE,
    PROJECT_DEVELOPER_ROLE,
    ARM_TEAM_ROLE
  })
  @Caching(
      evict = {
        @CacheEvict(value = "publicGetAllStakeholderCache", allEntries = true),
        @CacheEvict(value = "publicGetOneStakeholderCache", allEntries = true)
      })
  @PutMapping("/projects/{projectId}/requests")
  public ResponseEntity<Integer> updateRequest(
      @PathVariable(value = "projectId") Integer projectId,
      @Valid @RequestBody RequestCreateUpdateDTO update) {
    requestFacade.updateRequest(projectId, update);
    return ResponseEntity.ok().body(update.getId());
  }

  @GetMapping("/requests/required-document-types/{requestType}")
  public Map<String, Collection<RequestTypeDocumentTypeDTO>> getRequiredDocumentsForRequestType(
      @PathVariable(value = "requestType") Integer requestType) {
    var requestDocumentTypes = documentTypeService.getDocumentTypesForRequestType(requestType);
    return requestDocumentTypeConverter.toDtos(requestDocumentTypes);
  }
}
