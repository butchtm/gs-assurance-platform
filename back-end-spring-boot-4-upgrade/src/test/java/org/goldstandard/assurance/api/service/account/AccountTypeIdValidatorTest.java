package org.goldstandard.assurance.api.service.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTypeIdValidatorTest {

  @InjectMocks AccountTypeIdValidator validator;

  @Mock PermissionsService permissionsService;

  @Test
  void shouldReturnPermittedIdsIfRequestIdsIsEmpty() {
    Set<Integer> permittedIds = Set.of(1, 2, 3);
    when(permissionsService.getPermittedAccountTypeIds()).thenReturn(permittedIds);

    Collection<Integer> validIds = validator.validate(Collections.emptySet());

    assertThat(validIds).isEqualTo(permittedIds);
  }

  @Test
  void shouldReturnRequestedIdsIfPermittedIdsIsEmpty() {
    when(permissionsService.getPermittedAccountTypeIds()).thenReturn(Collections.emptySet());
    Set<Integer> requestedIds = Set.of(1, 3);

    Collection<Integer> validIds = validator.validate(requestedIds);

    assertThat(validIds).isEqualTo(requestedIds);
  }

  @Test
  void shouldReturnRequestedIdsIfAllPermitted() {
    when(permissionsService.getPermittedAccountTypeIds()).thenReturn(Set.of(1, 2, 3));
    Set<Integer> requestedIds = Set.of(1, 3);

    Collection<Integer> validIds = validator.validate(requestedIds);

    assertThat(validIds).isEqualTo(requestedIds);
  }

  @Test
  void shouldThrowExceptionIfAnyRequestedIdIsNotPermitted() {
    when(permissionsService.getPermittedAccountTypeIds()).thenReturn(Set.of(1, 3));
    Set<Integer> requestedIds = Set.of(1, 2);

    Throwable error = catchThrowable(() -> validator.validate(requestedIds));

    assertThat(error)
        .isInstanceOf(PermissionException.class)
        .hasMessage("account type ids [2] not permitted for user");
  }
}
