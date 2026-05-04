package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.FormDefinitionCreateDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionPublishDTO;
import org.goldstandard.assurance.api.dto.FormDefinitionUpdateDTO;
import org.goldstandard.assurance.api.service.form.FormDefinitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/form-definitions")
public class FormDefinitionController {

  private final FormDefinitionService service;

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping
  public ResponseEntity<FormDefinitionDTO> createFormDefinition(
      @Valid @RequestBody FormDefinitionCreateDTO dto) {
    var id = service.createFormDefinition(dto);
    return getFormDefinition(id);
  }

  @RolesAllowed(ADMIN_ROLE)
  @PutMapping("/{id}")
  public ResponseEntity<FormDefinitionDTO> updateFormDefinition(
      @PathVariable Integer id, @Valid @RequestBody FormDefinitionUpdateDTO dto) {
    service.updateFormDefinition(id, dto);
    return getFormDefinition(id);
  }

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping("/{id}/publications")
  public ResponseEntity<FormDefinitionDTO> publishFormDefinition(
      @PathVariable Integer id, @Valid @RequestBody FormDefinitionPublishDTO dto) {
    service.publishFormDefinition(id, dto);
    return getFormDefinition(id);
  }

  @RolesAllowed(ADMIN_ROLE)
  @GetMapping("/{id}")
  public ResponseEntity<FormDefinitionDTO> getFormDefinition(@PathVariable Integer id) {
    return ResponseEntity.ok(service.getFormDefinition(id));
  }
}
