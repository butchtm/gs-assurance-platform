package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.CommonDashboardConverter;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestDTO;
import org.goldstandard.assurance.api.dto.CommonDashboardRequestFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.CommonDashboardService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonDashboardRequestController {

  private final CommonDashboardService service;
  private final CommonDashboardConverter converter;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, SUPPORT_ROLE})
  @GetMapping("/common-dashboard-requests")
  public ResponseEntity<PagedResource<CommonDashboardRequestDTO>> getRequests(
      @ModelAttribute CommonDashboardRequestFiltersDTO filters,
      @PageableDefault(
              sort = {"requestStartDate"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    var requests = service.getCommonDashboardPage(filters, pageable);
    return ResponseEntity.ok(converter.toPagedResource(requests));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PostMapping(value = "/common-dashboard-requests/reports")
  public ResponseEntity<String> generateReport(
      @ModelAttribute CommonDashboardRequestFiltersDTO filters,
      @PageableDefault(
              sort = {"requestStartDate"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    service.generateReport(filters, pageable);
    return ResponseEntity.accepted().build();
  }
}
