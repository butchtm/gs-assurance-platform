package org.goldstandard.assurance.api.controller;

import static org.goldstandard.assurance.api.dto.common.PageableParams.DEFAULT_PAGE_SIZE;
import static org.goldstandard.assurance.api.service.PermissionsService.ADMIN_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.ARM_TEAM_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.FINANCE_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REGISTRY_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.GS_REVIEWER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.PROJECT_DEVELOPER_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.TAC_ROLE;
import static org.goldstandard.assurance.api.service.PermissionsService.VVB_ROLE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.AccountConverter;
import org.goldstandard.assurance.api.dto.AccountCreateUpdate;
import org.goldstandard.assurance.api.dto.AccountDTO;
import org.goldstandard.assurance.api.dto.AccountFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.account.AccountService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {

  private final AccountConverter converter;
  private final AccountService accountService;

  @RolesAllowed(ADMIN_ROLE)
  @PostMapping("/accounts")
  public ResponseEntity<Integer> createAccount(
      @Valid @RequestBody AccountCreateUpdate accountCreate) {
    var id = accountService.createAccount(accountCreate);
    return ResponseEntity.ok(id);
  }

  @RolesAllowed(ADMIN_ROLE)
  @PutMapping("/accounts/{accountId}")
  public ResponseEntity<Void> updateAccount(
      @PathVariable Integer accountId, @Valid @RequestBody AccountCreateUpdate accountUpdate) {
    accountService.updateAccount(accountId, accountUpdate);
    return ResponseEntity.noContent().build();
  }

  @RolesAllowed({ADMIN_ROLE, ARM_TEAM_ROLE, PROJECT_DEVELOPER_ROLE})
  @GetMapping("/accounts")
  public PagedResource<AccountDTO> getAccounts(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "assignmentTypeId", required = false) Integer assignmentTypeId,
      @RequestParam(value = "accountTypeIds", required = false) Collection<Integer> accountTypeIds,
      @PageableDefault(
              sort = {"id"},
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    AccountFiltersDTO filters =
        AccountFiltersDTO.builder()
            .email(email)
            .name(name)
            .active(active)
            .assignmentTypeId(assignmentTypeId)
            .accountTypeIds(accountTypeIds)
            .build();
    var accounts = accountService.getAccountsPage(filters, pageable);
    return converter.toPagedResource(accounts);
  }

  @RolesAllowed({
    ADMIN_ROLE,
    ARM_TEAM_ROLE,
    GS_REGISTRY_ROLE,
    FINANCE_ROLE,
    GS_REVIEWER_ROLE,
    PROJECT_DEVELOPER_ROLE,
    VVB_ROLE,
    TAC_ROLE
  })
  @GetMapping("/accounts/{accountId}")
  public ResponseEntity<AccountDTO> getAccountById(@PathVariable Integer accountId) {
    var account = accountService.getAccountById(accountId);
    return ResponseEntity.ok(converter.toDto(account));
  }
}
