package org.goldstandard.assurance.api.controller.test;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.NotificationConverter;
import org.goldstandard.assurance.api.dto.NotificationDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.notification.NotificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!production")
@RestController
@RequiredArgsConstructor
public class TestNotificationController {

  private final NotificationService service;
  private final NotificationConverter converter;

  @RolesAllowed(ADMIN_ROLE)
  @GetMapping("/notifications/emails")
  public ResponseEntity<PagedResource<NotificationDTO>> getNotifications(
      @PageableDefault(
              sort = {"createdTimestamp"},
              direction = Sort.Direction.DESC)
          Pageable pageable) {
    var notifications = service.getNotifications(pageable);
    return ResponseEntity.ok(converter.toPagedResource(notifications));
  }
}
