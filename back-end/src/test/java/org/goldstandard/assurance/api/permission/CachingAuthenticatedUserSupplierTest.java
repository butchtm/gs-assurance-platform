package org.goldstandard.assurance.api.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.goldstandard.assurance.api.entity.User;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.goldstandard.assurance.api.service.permission.CachingAuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CachingAuthenticatedUserSupplierTest {

  @InjectMocks CachingAuthenticatedUserSupplier cachingSupplier;

  @Mock AuthenticatedUserSupplier supplier;

  @Test
  void shouldReturnAuthenticatedUser() {
    var expectedUser = new User();
    when(supplier.getAuthenticatedUser()).thenReturn(Optional.of(expectedUser));

    var user = cachingSupplier.getAuthenticatedUser();

    assertThat(user).contains(expectedUser);
  }

  @Test
  void shouldOnlyRetrieveUserOnce() {
    var expectedUser = new User();
    when(supplier.getAuthenticatedUser()).thenReturn(Optional.of(expectedUser));

    cachingSupplier.getAuthenticatedUser();
    cachingSupplier.getAuthenticatedUser();
    var user = cachingSupplier.getAuthenticatedUser();

    assertThat(user).contains(expectedUser);
    verify(supplier, times(1)).getAuthenticatedUser();
  }

  @Test
  void shouldReturnAuthenticatedUserRoles() {
    Set<String> expectedRoles = Set.of("role-1", "role-2");
    when(supplier.getAuthenticatedUserRoles()).thenReturn(expectedRoles);

    var roles = cachingSupplier.getAuthenticatedUserRoles();

    assertThat(roles).isEqualTo(expectedRoles);
  }
}
