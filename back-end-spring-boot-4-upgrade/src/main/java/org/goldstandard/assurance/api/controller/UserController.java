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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.converter.AccountConverter;
import org.goldstandard.assurance.api.dto.UserDTO;
import org.goldstandard.assurance.api.dto.UserFiltersDTO;
import org.goldstandard.assurance.api.dto.common.PagedResource;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.user.UserFinder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserFinder userFinder;
  private final PermissionsService permissionsService;
  private final AccountConverter accountConverter;

  @RolesAllowed(ADMIN_ROLE)
  @GetMapping("/users")
  public List<UserDTO> getUsers(
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "searchTerm", required = false) String searchTerm) {
    UserFiltersDTO filters = UserFiltersDTO.builder().active(active).searchTerm(searchTerm).build();
    return userFinder.getAllUsers(filters).stream().map(accountConverter::toUserDto).toList();
  }

  @RolesAllowed(ADMIN_ROLE)
  @GetMapping("/users/pages")
  public PagedResource<UserDTO> getUserPages(
      @RequestParam(value = "active", required = false) Boolean active,
      @RequestParam(value = "searchTerm", required = false) String searchTerm,
      @PageableDefault(
              sort = {"userName"},
              size = DEFAULT_PAGE_SIZE)
          Pageable pageable) {
    UserFiltersDTO filters = UserFiltersDTO.builder().active(active).searchTerm(searchTerm).build();
    return new PagedResource<>(
        userFinder.getAllUsers(filters, pageable).map(accountConverter::toUserDto));
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
  @GetMapping("/user")
  public ResponseEntity<UserDTO> getUserByIdpUserId(
      @RequestParam(value = "idpUserId") UUID idpUserId) {
    if (!permissionsService.canAccessUser(idpUserId)) {
      // We're not going to signal that the user exists but you can't access it
      return ResponseEntity.notFound().build();
    }
    return userFinder
        .getUserByIdpUserId(idpUserId)
        .map(user -> ResponseEntity.ok(accountConverter.toUserDto(user)))
        .orElse(ResponseEntity.notFound().build());
  }
}
