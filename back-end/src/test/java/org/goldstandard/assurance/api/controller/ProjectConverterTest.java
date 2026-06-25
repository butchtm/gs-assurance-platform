package org.goldstandard.assurance.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.goldstandard.assurance.api.converter.ProjectConverter;
import org.goldstandard.assurance.api.converter.ProjectCreditingPeriodConverter;
import org.goldstandard.assurance.api.converter.ProjectLocationConverter;
import org.goldstandard.assurance.api.converter.TransitionDetailConverter;
import org.goldstandard.assurance.api.entity.Account;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.fixtures.ProjectMethodologyUpdateMother;
import org.goldstandard.assurance.api.service.PermissionsService;
import org.goldstandard.assurance.api.service.ProjectRiskSummaryService;
import org.goldstandard.assurance.api.service.external.registry.RegistryUiLinkFactory;
import org.goldstandard.assurance.api.service.pii.PiiMetadataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectConverterTest {

  @InjectMocks private ProjectConverter projectConverter;

  @Mock private TransitionDetailConverter transitionDetailConverter;
  @Mock private ProjectCreditingPeriodConverter creditingPeriodConverter;
  @Mock private ProjectLocationConverter projectLocationConverter;
  @Mock private PermissionsService permissionsService;
  @Mock private ProjectRiskSummaryService projectRiskSummaryService;
  @Mock private PiiMetadataService piiMetadataService;
  @Mock private RegistryUiLinkFactory registryLinkFactory;

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

  @Test
  void shouldDisplayRealCaseClassificationObjectOnDetailedDtoAndPreserveRawIdentityId() {
    var project =
        buildProject(
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
            "Real Case VPA - Cross Linking");
    when(registryLinkFactory.buildProjectLinkUrl(any(Project.class))).thenReturn(Optional.empty());

    var dto = projectConverter.toDetailedProjectDto(project);

    assertThat(dto.getProjectClassificationId())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);
    assertThat(dto.getProjectClassification().getId())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_ID);
    assertThat(dto.getProjectClassification().getCode())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_CODE);
    assertThat(dto.getProjectClassification().getDescription())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
  }

  @Test
  void shouldDisplayRegularClassificationObjectOnDetailedDtoAndPreserveRawIdentityId() {
    var project =
        buildProject(
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE,
            "Regular VPA - Cross Linking");
    when(registryLinkFactory.buildProjectLinkUrl(any(Project.class))).thenReturn(Optional.empty());

    var dto = projectConverter.toDetailedProjectDto(project);

    assertThat(dto.getProjectClassificationId())
        .isEqualTo(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
    assertThat(dto.getProjectClassification().getId())
        .isEqualTo(ProjectClassification.REGULAR_CASE_VPA_ID);
    assertThat(dto.getProjectClassification().getCode())
        .isEqualTo(ProjectClassification.REGULAR_VPA_CODE);
    assertThat(dto.getProjectClassification().getDescription())
        .isEqualTo(ProjectClassification.REGULAR_VPA_DESCRIPTION);
  }

  @Test
  void shouldPreserveRawProjectClassificationIdOnOverviewDto() {
    var project =
        buildProject(
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
            "Real Case VPA - Cross Linking");

    var dto = projectConverter.toOverviewProjectDto(project);

    assertThat(dto.getProjectClassificationId())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);
  }

  @Test
  void shouldPreserveRawProjectClassificationIdOnReferenceDto() {
    var project =
        buildProject(
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE,
            "Regular VPA - Cross Linking");

    var dto = projectConverter.toReferenceDto(project);

    assertThat(dto.getProjectClassificationId())
        .isEqualTo(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
  }

  private static Project buildProject(Integer classificationId, String code, String description) {
    var classification = new ProjectClassification();
    classification.setId(classificationId);
    classification.setCode(code);
    classification.setDescription(description);
    return Project.builder()
        .id(1)
        .gsid("GS1")
        .name("Project 1")
        .projectClassification(classification)
        .projectLocations(List.of())
        .projectAssets(List.of())
        .projectTypes(List.of())
        .sustainableDevelopmentGoals(List.of())
        .methodologies(List.of())
        .creditingPeriods(List.of())
        .build();
  }
}
