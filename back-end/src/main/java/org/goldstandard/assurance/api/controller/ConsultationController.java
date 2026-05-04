package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.ConsultationConverter;
import org.goldstandard.assurance.api.dto.ConsultationDTO;
import org.goldstandard.assurance.api.dto.ConsultationFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.StakeholderConsultationCompleter;
import org.goldstandard.assurance.api.service.StakeholderConsultationFinder;
import org.goldstandard.assurance.api.service.notification.ConsultationNotificationService;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsultationController {

  private final StakeholderConsultationCompleter completer;
  private final StakeholderConsultationFinder finder;
  private final ConsultationConverter converter;
  private final Clock clock;
  private final ConsultationNotificationService notificationService;
  private final AuthenticatedUserSupplier userSupplier;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PutMapping("/requests/{requestId}/live-stakeholder-consultations/completions")
  public ResponseEntity<Void> completeLiveRequestStakeholderConsultations(
      @PathVariable(value = "requestId") int requestId) {
    completer.completeLiveRequestStakeholderConsultations(requestId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/requests/{requestId}/live-stakeholder-consultations")
  public ResponseEntity<List<ConsultationDTO>> getLiveRequestStakeholderConsultations(
      @PathVariable(value = "requestId") int requestId) {
    var consultations = finder.getLiveStakeholderConsultations(requestId);
    return ResponseEntity.ok(converter.toDTOs(consultations));
  }

  @GetMapping("/requests/{requestId}/stakeholder-consultations")
  public ResponseEntity<List<ConsultationDTO>> getRequestStakeholderConsultations(
      @PathVariable(value = "requestId") int requestId) {
    var consultations = finder.getStakeholderConsultations(requestId);
    return ResponseEntity.ok(converter.toDTOs(consultations));
  }

  @GetMapping("/stakeholder-consultations/pages")
  public ResponseEntity<PagedResource<ConsultationDTO>> getStakeholderConsultationsPage(
      @RequestParam(value = "liveAt", required = false) ZonedDateTime liveAt,
      @RequestParam(value = "consultationTypeId", required = false) Integer consultationTypeId,
      Pageable pageable) {
    var filters =
        ConsultationFiltersDTO.builder()
            .liveAt(Optional.ofNullable(liveAt).orElseGet(() -> ZonedDateTime.now(clock)))
            .consultationTypeId(consultationTypeId)
            .build();
    var consultations = finder.getLiveStakeholderConsultations(filters, pageable);
    return ResponseEntity.ok(converter.toPagedDTOs(consultations));
  }

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping("/stakeholder-consultations/trigger-notifications")
  public ResponseEntity<Void> triggerNotificationsForLiveConsultations() {
    notificationService.sendConsultationNotifications(userSupplier.forceGetAuthenticatedUser());
    return ResponseEntity.ok().build();
  }
}
