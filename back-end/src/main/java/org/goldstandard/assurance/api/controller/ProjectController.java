package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.dto.*;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.service.ProjectRiskSummaryService;
import org.goldstandard.assurance.api.service.ProjectService;
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

@RequiredArgsConstructor
@RestController
public class ProjectController {

  private final ProjectService projectService;
  private final ProjectRiskSummaryService projectRiskSummaryService;
  private final ProjectConverter converter;

  @GetMapping("/projects")
  public PagedResource<ProjectDTO> getProjects(
      @PageableDefault(
              sort = {"gsidNum"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable,
      @RequestParam(value = "bundleId", required = false) Integer bundleId,
      @RequestParam(value = "name", required = false, defaultValue = "") String name,
      @RequestParam(value = "gsid", required = false, defaultValue = "") String gsid,
      @RequestParam(value = "accountId", required = false) Integer accountId,
      @RequestParam(value = "masterProjectId", required = false) Integer masterProjectId,
      @RequestParam(value = "parentProjectId", required = false) Integer parentProjectId,
      @RequestParam(value = "projectStatusId", required = false) Set<Integer> projectStatusId,
      @RequestParam(value = "projectIds", required = false) Set<Integer> projectIds,
      @RequestParam(value = "search", required = false, defaultValue = "") String search,
      @RequestParam(value = "minimal", required = false, defaultValue = "false") boolean minimal) {
    var dto =
        ProjectSearchCriteriaDTO.builder()
            .bundleId(bundleId)
            .name(name)
            .gsid(gsid)
            .accountId(accountId)
            .masterProjectId(masterProjectId)
            .parentProjectId(parentProjectId)
            .projectStatusId(projectStatusId)
            .projectIds(projectIds)
            .search(search)
            .build();
    if (minimal) {
      var projects = projectService.getProjectsMinimal(pageable, dto);
      return new PagedResource<>(projects.map(converter::toMinimalProjectDto));
    }
    var projects = projectService.getProjects(pageable, dto);
    return new PagedResource<>(projects.map(converter::toOverviewProjectDto));
  }

  @GetMapping("/projects/trees/pages")
  public PagedResource<ProjectTreeNodeDTO> getProjectTrees(
      @PageableDefault(
              sort = {"gsidNum"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable,
      @RequestParam(value = "bundleId", required = false) Integer bundleId,
      @RequestParam(value = "name", required = false, defaultValue = "") String name,
      @RequestParam(value = "gsid", required = false, defaultValue = "") String gsid,
      @RequestParam(value = "accountId", required = false) Integer accountId,
      @RequestParam(value = "masterProjectId", required = false) Integer masterProjectId,
      @RequestParam(value = "parentProjectId", required = false) Integer parentProjectId,
      @RequestParam(value = "projectStatusId", required = false) Set<Integer> projectStatusId,
      @RequestParam(value = "projectIds", required = false) Set<Integer> projectIds,
      @RequestParam(value = "search", required = false, defaultValue = "") String search) {
    var dto =
        ProjectSearchCriteriaDTO.builder()
            .bundleId(bundleId)
            .name(name)
            .gsid(gsid)
            .accountId(accountId)
            .masterProjectId(masterProjectId)
            .parentProjectId(parentProjectId)
            .projectStatusId(projectStatusId)
            .projectIds(projectIds)
            .search(search)
            .build();
    var projectTrees = projectService.getProjectTrees(pageable, dto);
    return new PagedResource<>(projectTrees.map(converter::toDto));
  }

  @GetMapping("/projects/{projectId}")
  public ResponseEntity<ProjectDTO> getProject(@PathVariable Integer projectId) {
    var project = projectService.getProjectById(projectId);
    return ResponseEntity.ok(converter.toDetailedProjectDto(project));
  }

  @GetMapping("/projects/gsid/{gsid}")
  public ResponseEntity<ProjectDTO> getProjectByGsId(@PathVariable String gsid) {
    var project = projectService.getProjectByGsId(gsid);
    return ResponseEntity.ok(converter.toDetailedProjectDto(project));
  }

  @GetMapping("/projects/{projectId}/trees")
  public ResponseEntity<ProjectTreeNodeDTO> getProjectTree(@PathVariable Integer projectId) {
    var tree = projectService.getProjectTree(projectId);
    return ResponseEntity.ok(converter.toDto(tree));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/projects")
  public ResponseEntity<Integer> createProject(@Valid @RequestBody ProjectCreateDTO createRequest) {
    var project = projectService.createProject(createRequest);
    return ResponseEntity.ok(project.getId());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/projects/candidate-poas")
  public ResponseEntity<Collection<ProjectDTO>> getCandidatePOAs(
      @RequestBody GetCandidatePOAsRequest request) {
    var projects = projectService.getCandidatePOAs(request);
    return ResponseEntity.ok(projects.stream().map(converter::toMinimalProjectDto).toList());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/projects/candidate-real-case-vpas")
  public ResponseEntity<Collection<ProjectDTO>> getCandidateRealCaseVPAs(
      @RequestBody GetCandidateRealCaseVPAsRequest request) {
    var projects = projectService.getCandidateRealCaseVPAs(request);
    return ResponseEntity.ok(projects.stream().map(converter::toMinimalProjectDto).toList());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PostMapping("/projects/candidate-parent-details")
  public ResponseEntity<CandidateParentProjectDetailsDTO> getCandidateParentDetails(
      @RequestBody GetCandidateParentProjectDetailsRequest request) {
    var project = projectService.getCandidateParentDetails(request);
    return ResponseEntity.ok(converter.toCandidateParentDetailsDto(project));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @PutMapping("/projects/{projectId}")
  public ResponseEntity<Integer> updateProject(
      @PathVariable(name = "projectId") Integer projectId,
      @Valid @RequestBody ProjectUpdateDTO updateDto) {
    var updatedProject = projectService.updateProject(projectId, updateDto);
    return ResponseEntity.ok(updatedProject.getId());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PutMapping("/projects/{projectId}/status")
  public ResponseEntity<Collection<Integer>> updateProjectStatus(
      @PathVariable(name = "projectId") Integer projectId,
      @Valid @RequestBody ProjectStatusUpdateDTO updateDto) {
    var updatedProjects = projectService.updateProjectStatus(projectId, updateDto);
    return ResponseEntity.ok(updatedProjects.stream().map(Project::getId).toList());
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE, SUPPORT_ROLE})
  @GetMapping("/projects/{projectId}/methodology-history/pages")
  public PagedResource<MethodologyHistoryDTO> getMethodologyHistory(
      @PathVariable Integer projectId,
      @RequestParam(value = "periodId", required = false) Integer periodId,
      @PageableDefault(
              sort = {"createdTimestamp"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    var history = projectService.getMethodologyHistory(projectId, periodId, pageable);
    return new PagedResource<>(history.map(converter::toMethodologyHistoryDTO));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, SUPPORT_ROLE})
  @GetMapping("/projects/{projectId}/risk-summaries")
  public PagedResource<ProjectRiskSummaryDTO> getProjectRiskSummaries(
      @PathVariable Integer projectId,
      @PageableDefault(
              sort = {"dateApplied"},
              direction = Sort.Direction.DESC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    var page = projectRiskSummaryService.getRiskSummary(projectId, pageable);
    return new PagedResource<>(page.map(converter::toDetailedProjectRiskSummaryDTO));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PostMapping("/projects/{projectId}/risk-summaries")
  public ResponseEntity<ProjectRiskSummaryDTO> createProjectRiskSummary(
      @PathVariable Integer projectId, @RequestBody @Valid ProjectRiskSummaryCreateDTO dto) {
    var entity = projectRiskSummaryService.create(projectId, dto);
    return ResponseEntity.ok(converter.toBasicProjectRiskSummaryDTO(entity));
  }
}
