package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goldstandard.assurance.api.converter.UserBatchConverter;
import org.goldstandard.assurance.api.dto.UserBatchDTO;
import org.goldstandard.assurance.api.dto.validator.ValidUserBatchCsvFile;
import org.goldstandard.assurance.api.entity.UserBatch;
import org.goldstandard.assurance.api.exception.BatchRequestException;
import org.goldstandard.assurance.api.service.user.UserBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.dataformat.csv.CsvReadException;

@RolesAllowed(ADMIN_ROLE)
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/batches")
@Slf4j
@Validated
public class UserBatchController {

  private final UserBatchService service;
  private final UserBatchConverter mapper;

  @PostMapping
  public ResponseEntity<UserBatchDTO> create(
      @ValidUserBatchCsvFile @RequestParam("data") MultipartFile csv) {
    try {
      UserBatch batch = service.create(mapper.toCreateUserRequests(csv.getInputStream()));
      return ResponseEntity.accepted().body(mapper.toApiUserBatch(batch));
    } catch (CsvReadException e) {
      throw new BatchRequestException("Invalid csv file uploaded");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @GetMapping("/{id}")
  public UserBatchDTO get(@PathVariable Integer id) {
    return mapper.toApiUserBatch(service.get(id));
  }
}
