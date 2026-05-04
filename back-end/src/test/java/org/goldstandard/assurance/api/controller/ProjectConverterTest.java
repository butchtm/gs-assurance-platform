package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.fixtures.ProjectMethodologyUpdateMother;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectConverterTest {

  @InjectMocks private ProjectConverter projectConverter;

  @Mock private PermissionsService permissionsService;

  @Test
  void shouldPopulateAccountFieldsOnMethodologyHistoryIfAdminOrArmUser() {
    var methodologyUpdate = ProjectMethodologyUpdateMother.build();
    when(permissionsService.isAdminOrArm()).thenReturn(true);

    var convertedHistory = projectConverter.toMethodologyHistoryDTO(methodologyUpdate);

    assertThat(convertedHistory.getUpdatedByAccountId())
        .isEqualTo(methodologyUpdate.getCreatedBy().map(Account::getId).orElse(null));
    assertThat(convertedHistory.getUpdatedByAccountName())
        .isEqualTo(methodologyUpdate.getCreatedBy().map(Account::getName).orElse(null));
  }

  @Test
  void shouldNotPopulateAccountFieldsOnMethodologyHistoryIfNotAdminOrArmUser() {
    var methodologyUpdate = ProjectMethodologyUpdateMother.build();
    when(permissionsService.isAdminOrArm()).thenReturn(false);

    var convertedHistory = projectConverter.toMethodologyHistoryDTO(methodologyUpdate);

    assertThat(convertedHistory.getUpdatedByAccountId()).isNull();
    assertThat(convertedHistory.getUpdatedByAccountName()).isNull();
  }
}
