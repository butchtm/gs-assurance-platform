package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.SUPPORT_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.PathwayGroupTimelineConverter;
import org.goldstandard.assurance.api.converter.RequestActivityConverter;
import org.goldstandard.assurance.api.dto.PathwayGroupTimelineActivityDTO;
import org.goldstandard.assurance.api.dto.RequestActivityDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.activity.RequestActivityService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestActivitiesController {

  private final RequestActivityService service;
  private final RequestActivityConverter mapper;
  private final PathwayGroupTimelineConverter timelineConverter;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PostMapping("/request-activities/{requestActivityId}/completions")
  public ResponseEntity<Void> completions(
      @PathVariable(value = "requestActivityId") Integer requestActivityId) {
    service.complete(requestActivityId);
    return ResponseEntity.ok().build();
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, SUPPORT_ROLE})
  @GetMapping("/requests/{requestId}/activities/pages")
  public PagedResource<RequestActivityDTO> getRequestActivityHistory(
      @PathVariable(value = "requestId") Integer requestId,
      @PageableDefault(
              sort = {"startDate"},
              size = DEFAULT_PAGE_SIZE,
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    return new PagedResource<>(service.findByRequestId(requestId, pageable).map(mapper::toDto));
  }

  @GetMapping("/requests/{requestId}/current-pathway-group-timelines")
  public Collection<PathwayGroupTimelineActivityDTO> getRequestCurrentPathwayGroupTimeline(
      @PathVariable(value = "requestId") Integer requestId) {
    var timeline = service.getCurrentPathwayGroupTimeline(requestId);
    return timelineConverter.toActivityDtos(timeline);
  }
}
