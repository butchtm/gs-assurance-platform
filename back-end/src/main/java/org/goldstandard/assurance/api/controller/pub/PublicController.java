package org.goldstandard.assurance.api.controller.pub;

import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.controller.DocumentDownloadResponseFactory;
import org.goldstandard.assurance.api.converter.BatchRequestConverter;
import org.goldstandard.assurance.api.converter.PiiAnalysisConverter;
import org.goldstandard.assurance.api.dto.BatchProjectsDTO;
import org.goldstandard.assurance.api.dto.CommentCreateDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisDTO;
import org.goldstandard.assurance.api.dto.TextAnalysisResultsDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.dto.pub.PublicProject;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectBasicDTO;
import org.goldstandard.assurance.api.dto.pub.stakeholder.project.PublicStakeholderProjectDetailDTO;
import org.goldstandard.assurance.api.exception.NotFoundException;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.BatchRequestFinder;
import org.goldstandard.assurance.api.service.CommentService;
import org.goldstandard.assurance.api.service.document.DocumentService;
import org.goldstandard.assurance.api.service.pii.PiiAnalysisService;
import org.goldstandard.assurance.api.service.pub.PublicService;
import org.goldstandard.assurance.api.service.pub.stakeholder.PublicStakeholderFinderBasic;
import org.goldstandard.assurance.api.service.pub.stakeholder.PublicStakeholderFinderDetail;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public")
@RequiredArgsConstructor
public class PublicController {

  private final PublicService publicService;
  private final PublicStakeholderFinderBasic basicFinder;
  private final PublicStakeholderFinderDetail detailFinder;
  private final CommentService commentService;
  private final BatchRequestFinder batchRequestFinder;
  private final BatchRequestConverter batchRequestConverter;
  private final DocumentService documentService;
  private final DocumentDownloadResponseFactory downloadResponseFactory;
  private final PiiAnalysisService piiAnalysisService;
  private final PiiAnalysisConverter piiAnalysisConverter;

  @GetMapping("/project-documents/{gsid}")
  public ResponseEntity<PublicProject> getPublicProjectRequestDocuments(
      @PathVariable(value = "gsid") String gsid) {
    var project = publicService.getPublicProjectRequestDocumentsForGSID(gsid);
    return ResponseEntity.ok(project);
  }

  @Cacheable(value = "publicGetAllStakeholderCache")
  @GetMapping("/consultations/pages")
  public ResponseEntity<PagedResource<PublicStakeholderProjectBasicDTO>> getProjectsInConsultation(
      Pageable pageable) {
    var projects = basicFinder.getAllBasicProjectsInConsultation(pageable);
    return ResponseEntity.ok(new PagedResource<>(projects));
  }

  @Cacheable(value = "publicGetOneStakeholderCache", key = "#gsid")
  @GetMapping("/consultations/{gsid}")
  public ResponseEntity<PublicStakeholderProjectDetailDTO> getProjectDetailInConsultation(
      @PathVariable String gsid) {
    var project = detailFinder.getProjectDetailsInConsultation(gsid);
    return ResponseEntity.ok(project);
  }

  @GetMapping("/requests/{requestId}/gsid")
  public ResponseEntity<String> getRequestProjectGsId(
      @PathVariable(value = "requestId") Integer requestId) {
    return ResponseEntity.ok().body(publicService.getRequestProjectGsId(requestId));
  }

  @GetMapping("/batch/{batchId}/projects")
  public ResponseEntity<BatchProjectsDTO> getAllProjectsInBatch(@PathVariable int batchId) {
    var batchRequests = batchRequestFinder.getRequestsInBatch(batchId);
    return ResponseEntity.ok(batchRequestConverter.toPublicBatchProjects(batchRequests));
  }

  @PostMapping("/comments")
  public ResponseEntity<Integer> saveNewComment(@RequestBody CommentCreateDTO newComment) {
    return ResponseEntity.ok(commentService.saveNewComment(newComment).getId());
  }

  @GetMapping("/documents/{documentId}/download")
  public ResponseEntity<Void> downloadDocument(
      @PathVariable UUID documentId, HttpServletResponse response) {
    try {
      var document = documentService.getDocument(documentId);
      downloadResponseFactory.addDocumentToResponse(document, response);
      return ResponseEntity.ok().build();
    } catch (PermissionException e) {
      throw new NotFoundException(String.format("document not found with id %s", documentId), e);
    }
  }

  @PostMapping("/pii-analysis")
  public ResponseEntity<TextAnalysisResultsDTO> analyseText(
      @RequestBody TextAnalysisDTO textAnalysis) {
    return ResponseEntity.ok(piiAnalysisConverter.toDTO(piiAnalysisService.analyse(textAnalysis)));
  }
}
