package org.goldstandard.assurance.api.controller.test;

import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.service.account.AccountService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Profile({"!production"})
@RestController
@RequiredArgsConstructor
public class TestAccountController {

  private final AccountService accountService;

  @RolesAllowed(ADMIN_ROLE)
  @DeleteMapping("/accounts/{accountId}")
  public ResponseEntity<Void> deleteAccount(@PathVariable Integer accountId) {
    accountService.deleteAccount(accountId);
    return ResponseEntity.noContent().build();
  }
}
