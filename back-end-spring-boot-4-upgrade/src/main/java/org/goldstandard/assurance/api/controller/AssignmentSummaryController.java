package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.util.DocumentUtils.EXCEL_XLSX;
import static org.springframework.data.domain.Sort.Direction.ASC;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.AssignmentSummaryFiltersDTO;
import org.goldstandard.assurance.api.dto.AssignmentSummaryResponseDTO;
import org.goldstandard.assurance.api.service.AssignmentSummaryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/assignment-summaries")
@RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
public class AssignmentSummaryController {

  private final AssignmentSummaryService service;
  private final DownloadResponseHeaderPopulator headerPopulator;

  @GetMapping("/arm")
  public ResponseEntity<AssignmentSummaryResponseDTO> getArmAssignmentSummary(
      @ModelAttribute AssignmentSummaryFiltersDTO filters,
      @SortDefault(sort = "accountName", direction = ASC) Pageable pageable) {
    return ResponseEntity.ok(service.getArmAssignmentSummaryResponse(filters, pageable));
  }

  @GetMapping("/review")
  public ResponseEntity<AssignmentSummaryResponseDTO> getReviewAssignmentSummary(
      @ModelAttribute AssignmentSummaryFiltersDTO filters,
      @SortDefault(sort = "accountName", direction = ASC) Pageable pageable) {
    return ResponseEntity.ok(service.getReviewAssignmentSummaryResponse(filters, pageable));
  }

  @PostMapping(value = "/reports/review")
  public ResponseEntity<byte[]> generateReport(
      @ModelAttribute AssignmentSummaryFiltersDTO filters,
      @PageableDefault(
              sort = {"accountName"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.ASC)
          Pageable pageable) {
    var assignmentSummaryReport = service.generateReviewReport(filters, pageable);
    return headerPopulator
        .populate(ResponseEntity.ok(), assignmentSummaryReport.getFilename())
        .contentType(MediaType.parseMediaType(EXCEL_XLSX))
        .body(assignmentSummaryReport.getData());
  }

  @PostMapping(value = "/reports/arm")
  public ResponseEntity<byte[]> generateArmReport(
      @ModelAttribute AssignmentSummaryFiltersDTO filters,
      @PageableDefault(
              sort = {"accountName"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.ASC)
          Pageable pageable) {
    var assignmentSummaryReport = service.generateArmReport(filters, pageable);
    return headerPopulator
        .populate(ResponseEntity.ok(), assignmentSummaryReport.getFilename())
        .contentType(MediaType.parseMediaType(EXCEL_XLSX))
        .body(assignmentSummaryReport.getData());
  }
}
