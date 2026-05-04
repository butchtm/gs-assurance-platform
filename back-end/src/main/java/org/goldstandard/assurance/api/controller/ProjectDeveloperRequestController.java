package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.GetProjectDeveloperRequestsDTO;
import org.goldstandard.assurance.api.dto.ProjectDTO;
import org.goldstandard.assurance.api.dto.ProjectDeveloperRequestDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.ProjectDeveloperRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project-developer-requests")
public class ProjectDeveloperRequestController {

  private final ProjectDeveloperRequestService service;

  @RolesAllowed({PROJECT_DEVELOPER_ROLE})
  @PostMapping("/pages")
  public ResponseEntity<PagedResource<ProjectDeveloperRequestDTO>> getRequestPage(
      @RequestBody GetProjectDeveloperRequestsDTO getRequestsDto,
      @PageableDefault(
              sort = {"id"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    var page = service.getProjectDeveloperRequests(getRequestsDto, pageable);
    return ResponseEntity.ok(new PagedResource<>(page));
  }

  @RolesAllowed({PROJECT_DEVELOPER_ROLE})
  @GetMapping("/projects")
  public ResponseEntity<Collection<ProjectDTO>> getRequestProjects() {
    return ResponseEntity.ok(service.getProjectDeveloperRequestProjects());
  }
}
