package org.goldstandard.assurance.api.converter;

import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountFieldAccessPolicy {

  private final PermissionsService permissionsService;

  public boolean shouldHideSensitiveData(Account account) {
    if (permissionsService.isAdminOrArm()) {
      return false;
    }
    return !permissionsService.currentUserInAccount(account.getId());
  }
}
