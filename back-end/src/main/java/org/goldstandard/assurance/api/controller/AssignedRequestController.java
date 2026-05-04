package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.AccountConverter;
import org.goldstandard.assurance.api.converter.AssignedRequestConverter;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.AssignedRequestDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.lookup.ExtendedReferenceDataType;
import org.goldstandard.assurance.api.service.AssignedRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AssignedRequestController {

  private final AssignedRequestService assignedRequestService;
  private final AssignedRequestConverter requestConverter;
  private final AccountConverter accountConverter;
  private final ProjectConverter projectConverter;

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests")
  public ResponseEntity<PagedResource<AssignedRequestDTO>> getAssignedRequests(
      @RequestParam(value = "requestStatusIds", required = false)
          Collection<Integer> requestStatusIds,
      @RequestParam(value = "projectIds", required = false) Collection<Integer> projectIds,
      @RequestParam(value = "projectAccountIds", required = false)
          Collection<Integer> projectAccountIds,
      @RequestParam(value = "reviewerAccountIds", required = false)
          Collection<Integer> reviewerAccountIds,
      @RequestParam(value = "vvbAccountIds", required = false) Collection<Integer> vvbAccountIds,
      @RequestParam(value = "invoiceStatusIds", required = false)
          Collection<Integer> invoiceStatusIds,
      @RequestParam(value = "requestTypeIds", required = false) Collection<Integer> requestTypeIds,
      @RequestParam(value = "projectSearch", required = false) String projectSearch,
      @PageableDefault(size = DEFAULT_PAGE_SIZE)
          @SortDefault(sort = "lastRequestStatusChangeDate", direction = DESC)
          @SortDefault(sort = "requestId", direction = DESC)
          Pageable pageable) {
    var filters =
        SpecificDashboardRequestFiltersDTO.builder()
            .requestStatusIds(requestStatusIds)
            .projectIds(projectIds)
            .projectAccountIds(projectAccountIds)
            .reviewerAccountIds(reviewerAccountIds)
            .vvbAccountIds(vvbAccountIds)
            .invoiceStatusIds(invoiceStatusIds)
            .requestTypeIds(requestTypeIds)
            .projectSearch(projectSearch)
            .build();
    var assignedRequests = assignedRequestService.getAssignedRequests(filters, pageable);
    return ResponseEntity.ok(requestConverter.toPagedResource(assignedRequests));
  }

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests/request-statuses")
  public ResponseEntity<Collection<ExtendedReferenceDataType>> getRequestStatuses() {
    return ResponseEntity.ok(assignedRequestService.getAssignedRequestStatuses());
  }

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests/invoice-statuses")
  public ResponseEntity<Collection<ExtendedReferenceDataType>> getInvoiceStatuses() {
    return ResponseEntity.ok(assignedRequestService.getAssignedRequestInvoiceStatuses());
  }

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests/projects")
  public ResponseEntity<Collection<ProjectDTO>> getProjects() {
    var projects = assignedRequestService.getAssignedRequestProjects();
    return ResponseEntity.ok(projects.stream().map(projectConverter::toMinimalProjectDto).toList());
  }

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests/accounts")
  public ResponseEntity<Collection<AccountDTO>> getAccounts(
      @RequestParam(value = "accountTypeId") Integer accountTypeId) {
    var accounts = assignedRequestService.getAssignedRequestAccounts(accountTypeId);
    return ResponseEntity.ok(accountConverter.toDtosWithoutUsers(accounts));
  }

  @RolesAllowed({ARM_TEAM_ROLE, FINANCE_ROLE, GS_REVIEWER_ROLE, GS_REGISTRY_ROLE, VVB_ROLE})
  @GetMapping("/assigned-requests/request-types")
  public ResponseEntity<Collection<ExtendedReferenceDataType>> getRequestTypes() {
    return ResponseEntity.ok(assignedRequestService.getAssignedRequestTypes());
  }
}
