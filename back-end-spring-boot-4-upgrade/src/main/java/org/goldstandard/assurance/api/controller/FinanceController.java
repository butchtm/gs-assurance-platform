package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.finance.FinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class FinanceController {

  private final FinanceService service;

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping("/requests/{requestId}/sales-orders")
  public ResponseEntity<Void> createSalesOrder(@PathVariable Integer requestId) {
    service.triggerRequestSalesOrderIfPossible(requestId);
    return ResponseEntity.accepted().build();
  }

  @RolesAllowed({ADMIN_ROLE})
  @PostMapping("/projects/{gsid}/contracts")
  @ResponseBody
  public ResponseEntity<Void> createProjectContract(@PathVariable String gsid) {
    service.triggerProjectContractIfPossible(gsid);
    return ResponseEntity.accepted().build();
  }
}
