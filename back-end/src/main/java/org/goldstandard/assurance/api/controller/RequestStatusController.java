package org.goldstandard.assurance.api.controller;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.dto.RequestStatusDetail;
import org.goldstandard.assurance.api.service.RequestStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestStatusController {

  private final RequestStatusService requestStatusService;

  @GetMapping("/request-status/{requestStatusId}")
  public ResponseEntity<RequestStatusDetail> getDetailForRequestStatus(
      @PathVariable(value = "requestStatusId") Integer requestStatusId) {
    return requestStatusService
        .getDetailForRequestStatus(requestStatusId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
