package org.goldstandard.assurance.api.service.account;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountTypeIdValidator {
  private final PermissionsService permissionsService;

  public Collection<Integer> validate(Collection<Integer> requestedIds) {
    Collection<Integer> permittedIds = permissionsService.getPermittedAccountTypeIds();
    if (CollectionUtils.isEmpty(permittedIds)) {
      return requestedIds;
    }
    if (CollectionUtils.isEmpty(requestedIds)) {
      return permittedIds;
    }
    Collection<Integer> notPermittedIds = CollectionUtils.subtract(requestedIds, permittedIds);
    if (notPermittedIds.isEmpty()) {
      return requestedIds;
    }
    throw new PermissionException("account type ids %s not permitted for user", notPermittedIds);
  }
}
