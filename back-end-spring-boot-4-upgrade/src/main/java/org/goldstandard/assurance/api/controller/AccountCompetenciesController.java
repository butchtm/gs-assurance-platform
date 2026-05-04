package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.CompetenciesDTO;
import org.goldstandard.assurance.api.service.account.competencies.CompetenciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("accounts")
public class AccountCompetenciesController {

  private final CompetenciesService service;

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PostMapping("/{accountId}/competencies")
  public ResponseEntity<Integer> createCompetencies(
      @PathVariable Integer accountId, @Validated @RequestBody CompetenciesDTO competenciesDto) {
    return ResponseEntity.ok(service.createCompetencies(accountId, competenciesDto));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @PutMapping("/{accountId}/competencies")
  public ResponseEntity<Integer> updateCompetencies(
      @PathVariable Integer accountId, @Validated @RequestBody CompetenciesDTO competenciesDto) {
    return ResponseEntity.ok(service.updateCompetencies(accountId, competenciesDto));
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
  @GetMapping("/{accountId}/competencies")
  public ResponseEntity<Optional<CompetenciesDTO>> getCompetenciesByAccountId(
      @PathVariable Integer accountId) {
    return ResponseEntity.ok(service.getCompetencyByAccountId(accountId));
  }
}
