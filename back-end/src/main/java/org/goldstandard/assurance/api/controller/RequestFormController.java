package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.dto.RequestFormCreateDTO;
import org.goldstandard.assurance.api.dto.RequestFormDTO;
import org.goldstandard.assurance.api.dto.RequestFormFiltersDTO;
import org.goldstandard.assurance.api.dto.RequestFormSubmitDTO;
import org.goldstandard.assurance.api.dto.RequestFormUpdateDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.form.RequestFormService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestFormController {

  private final RequestFormService service;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/requests/{requestId}/forms")
  public ResponseEntity<RequestFormDTO> createRequestForm(
      @PathVariable Integer requestId, @Valid @RequestBody RequestFormCreateDTO dto) {
    var formId = service.createRequestForm(requestId, dto);
    return getRequestForm(requestId, formId);
  }

  @GetMapping(value = "/requests/{requestId}/forms/pages")
  public ResponseEntity<PagedResource<RequestFormDTO>> getPaginatedRequestForms(
      @PageableDefault(
              sort = {"formId"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable,
      @PathVariable Integer requestId,
      @ModelAttribute RequestFormFiltersDTO filters) {
    return ResponseEntity.ok(service.getRequestForms(requestId, filters, pageable));
  }

  @GetMapping(value = "/requests/{requestId}/forms")
  public ResponseEntity<Collection<RequestFormDTO>> getRequestForms(
      @PathVariable Integer requestId, @ModelAttribute RequestFormFiltersDTO filters) {
    return ResponseEntity.ok(service.getRequestForms(requestId, filters));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PutMapping("/requests/{requestId}/forms/{formId}")
  public ResponseEntity<RequestFormDTO> createRequestForm(
      @PathVariable Integer requestId,
      @PathVariable Integer formId,
      @Valid @RequestBody RequestFormUpdateDTO dto) {
    service.updateRequestForm(requestId, formId, dto);
    return getRequestForm(requestId, formId);
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/requests/{requestId}/forms/{formId}/submissions")
  public ResponseEntity<RequestFormDTO> submitRequestForm(
      @PathVariable Integer requestId,
      @PathVariable Integer formId,
      @Valid @RequestBody RequestFormSubmitDTO dto) {
    service.submitRequestForm(requestId, formId, dto);
    return getRequestForm(requestId, formId);
  }

  @GetMapping("/requests/{requestId}/forms/{formId}")
  public ResponseEntity<RequestFormDTO> getRequestForm(
      @PathVariable Integer requestId, @PathVariable Integer formId) {
    return ResponseEntity.ok(service.getRequestForm(requestId, formId));
  }
}
