package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.dto.ProjectUpdateDTO;
import org.goldstandard.assurance.api.fixtures.ProjectMother;
import org.goldstandard.assurance.api.service.permission.AuthenticatedUserSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectUpdateApplierTest {

  private static final Integer AUTHENTICATED_USER_ID = 348543;

  @InjectMocks ProjectUpdateApplier applier;

  @Mock AuthenticatedUserSupplier authenticatedUserSupplier;

  @Test
  void updateProjectSetsUpdatedByUserId() {
    var originalProject = ProjectMother.build();
    when(authenticatedUserSupplier.forceGetAuthenticatedUserPlatformId())
        .thenReturn(AUTHENTICATED_USER_ID);
    var update =
        ProjectUpdateDTO.builder()
            .projectClassificationId(originalProject.getProjectClassification().getId())
            .accountId(originalProject.getAccountId())
            .build();

    var updatedProject = applier.applyUpdates(originalProject, update);

    assertThat(updatedProject)
        .usingRecursiveComparison()
        .ignoringFields("updatedByUserId")
        .isEqualTo(originalProject);
    assertThat(updatedProject.getUpdatedByUserId()).isEqualTo(AUTHENTICATED_USER_ID);
  }
}
