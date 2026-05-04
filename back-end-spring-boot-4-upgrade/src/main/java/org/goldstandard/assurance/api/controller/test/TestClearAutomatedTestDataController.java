package org.goldstandard.assurance.api.controller.test;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"!production"})
@RestController
@RequiredArgsConstructor
public class TestClearAutomatedTestDataController {
  private final TestClearAutomatedTestDataService testClearAutomatedTestDataService;

  @RolesAllowed({ADMIN_ROLE})
  @DeleteMapping("/automated-test-projects")
  public ResponseEntity<Void> deleteAutomatedTestProjects() {
    testClearAutomatedTestDataService.deleteAllAutomatedTestData();
    return ResponseEntity.noContent().build();
  }
}
