package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.MethodologyConverter;
import org.goldstandard.assurance.api.dto.MethodologyManagementDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.service.MethodologyService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE})
@RequestMapping("/methodologies")
@RestController
@RequiredArgsConstructor
public class MethodologyController {

  private final MethodologyService service;
  private final MethodologyConverter converter;

  @GetMapping("/pages")
  public PagedResource<MethodologyManagementDTO> getAllMethodologies(
      @RequestParam(value = "description", required = false, defaultValue = "") String description,
      @RequestParam(value = "paa", required = false) Boolean paa,
      @PageableDefault(
              sort = {"id"},
              direction = Sort.Direction.ASC,
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    var allMethodologies = service.getAll(pageable, description, paa);
    return new PagedResource<>(allMethodologies.map(converter::toManagementDTO));
  }

  @PermitAll
  @GetMapping
  public ResponseEntity<Collection<Methodology>> getAllMethodologies() {
    var allMethodologies = service.getAll();
    return ResponseEntity.ok(allMethodologies);
  }

  @PostMapping
  public ResponseEntity<MethodologyManagementDTO> createMethodology(
      @RequestBody MethodologyManagementDTO methodology) {
    var created = service.create(methodology);
    return ResponseEntity.ok(converter.toManagementDTO(created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<MethodologyManagementDTO> updateMethodology(
      @PathVariable Integer id, @RequestBody MethodologyManagementDTO methodology) {
    var updated = service.update(id, methodology);
    return ResponseEntity.ok(converter.toManagementDTO(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMethodology(@PathVariable Integer id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
