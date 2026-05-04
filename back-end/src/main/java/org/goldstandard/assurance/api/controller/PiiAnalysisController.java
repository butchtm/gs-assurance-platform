package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

import jakarta.annotation.security.RolesAllowed;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.PiiAnalysisConverter;
import org.goldstandard.assurance.api.dto.DocumentPiiScanCriteriaDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.document.pii.analysis.DocumentPiiAnalysisService;
import org.goldstandard.assurance.api.service.document.pii.analysis.PageGroupedPii;
import org.goldstandard.assurance.api.service.pii.PiiAnalysisService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pii-analysis")
@Slf4j
public class PiiAnalysisController {

  private final PiiAnalysisService service;
  private final DocumentPiiAnalysisService documentAnalysisService;
  private final PiiAnalysisConverter converter;

  @PostMapping
  @RolesAllowed(value = {ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE, TAC_ROLE})
  public ResponseEntity<TextAnalysisResultsDTO> analyseText(
      @RequestBody TextAnalysisDTO textAnalysis) {
    return ResponseEntity.ok(converter.toDTO(service.analyse(textAnalysis)));
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE,
    GS_REVIEWER_ROLE
  })
  @GetMapping("/documents/supported")
  public ResponseEntity<Boolean> isDocumentSupported(
      @ModelAttribute DocumentPiiScanCriteriaDTO scanCriteria) {
    return ResponseEntity.ok(
        documentAnalysisService.isDocumentScannable(converter.toAnalysisCriteria(scanCriteria)));
  }

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping("/documents/{documentId}")
  public ResponseEntity<Void> analyseDocument(@PathVariable("documentId") UUID documentId) {
    documentAnalysisService.startAnalysis(documentId);
    return ResponseEntity.accepted().build();
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, GS_REGISTRY_ROLE, PROJECT_DEVELOPER_ROLE, VVB_ROLE})
  @GetMapping("/documents/{documentId}/results")
  public PagedResource<PageGroupedPii> getAnalysisResults(
      @PathVariable("documentId") UUID documentId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE)
          @SortDefault(sort = "pageNumber", direction = ASC)
          @SortDefault(sort = "entity", direction = ASC)
          Pageable pageable) {
    var piiResults = documentAnalysisService.getPiiResultsPage(documentId, pageable);
    return new PagedResource<>(piiResults.map(converter::toPageGroupedPii));
  }
}
