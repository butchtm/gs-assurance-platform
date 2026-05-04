package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.AllRequestConverter;
import org.goldstandard.assurance.api.dto.AllRequestsDTO;
import org.goldstandard.assurance.api.dto.SpecificDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.AllRequestFinder;
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
public class AllRequestController {

  private final AllRequestFinder finder;
  private final AllRequestConverter converter;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @GetMapping("/all-requests")
  public ResponseEntity<PagedResource<AllRequestsDTO>> getAllRequests(
      @RequestParam(value = "requestStatusIds", required = false)
          Collection<Integer> requestStatusIds,
      @RequestParam(value = "projectIds", required = false) Collection<Integer> projectIds,
      @RequestParam(value = "projectAccountIds", required = false)
          Collection<Integer> projectAccountIds,
      @RequestParam(value = "reviewerAccountIds", required = false)
          Collection<Integer> reviewerAccountIds,
      @RequestParam(value = "vvbAccountIds", required = false) Collection<Integer> vvbAccountIds,
      @RequestParam(value = "qaqcAccountIds", required = false) Collection<Integer> qaqcAccountIds,
      @RequestParam(value = "invoiceStatusIds", required = false)
          Collection<Integer> invoiceStatusIds,
      @RequestParam(value = "activityIds", required = false) Collection<Integer> activityIds,
      @RequestParam(value = "responsibleAccountIds", required = false)
          Collection<Integer> responsibleAccountIds,
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
            .activityIds(activityIds)
            .responsibleAccountIds(responsibleAccountIds)
            .requestTypeIds(requestTypeIds)
            .projectSearch(projectSearch)
            .qaqcAccountIds(qaqcAccountIds)
            .build();
    var allRequests = finder.getAllRequestsPage(filters, pageable);
    return ResponseEntity.ok(converter.toPagedResource(allRequests));
  }
}
