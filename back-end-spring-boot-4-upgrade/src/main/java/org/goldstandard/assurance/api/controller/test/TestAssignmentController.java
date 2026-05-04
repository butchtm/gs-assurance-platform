package org.goldstandard.assurance.api.controller.test;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.repository.RequestAccountAssignmentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequiredArgsConstructor
public class TestAssignmentController {

  private final RequestAccountAssignmentRepository repository;

  @DeleteMapping("/assignments/{id}")
  public ResponseEntity<Void> deleteAssignment(@PathVariable Integer id) {
    repository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
