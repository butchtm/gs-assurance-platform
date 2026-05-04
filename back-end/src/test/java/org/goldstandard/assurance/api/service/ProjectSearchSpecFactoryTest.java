package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.goldstandard.assurance.api.entity.ProjectSearchCriteriaDTO;
import org.goldstandard.assurance.api.exception.PermissionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectSearchSpecFactoryTest {

  @InjectMocks private ProjectSearchSpecFactory factory;

  @Mock private PermissionsService permissionsService;

  @Mock private ProjectSearchCriteriaDTO criteria;

  @Test
  void shouldThrowExceptionIfUserIsProjectDeveloperWithoutAnAccount() {
    when(permissionsService.isAdminOrFinanceOrArmOrTacOrRegistry()).thenReturn(false);
    when(permissionsService.isProjectDeveloper()).thenReturn(true);
    when(permissionsService.getAccountIdForUser()).thenReturn(Optional.empty());

    Throwable error = catchThrowable(() -> factory.build(criteria));

    assertThat(error).isInstanceOf(PermissionException.class);
  }
}
