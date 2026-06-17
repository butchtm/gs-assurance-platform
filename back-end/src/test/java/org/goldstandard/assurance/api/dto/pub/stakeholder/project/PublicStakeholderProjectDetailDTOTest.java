package org.goldstandard.assurance.api.dto.pub.stakeholder.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.view.stakeholder.project.ProjectBase;
import org.junit.jupiter.api.Test;

class PublicStakeholderProjectDetailDTOTest {

  @Test
  void shouldNormalizeRealCaseCrossLinkClassificationDescription() {
    var project = project("Real Case VPA (cross linkage)");

    var dto = new PublicStakeholderProjectDetailDTO(project);

    assertThat(dto.getProjectClassification())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
  }

  @Test
  void shouldNormalizeRegularCrossLinkClassificationDescription() {
    var project = project("Regular Case VPA (cross linkage)");

    var dto = new PublicStakeholderProjectDetailDTO(project);

    assertThat(dto.getProjectClassification())
        .isEqualTo(ProjectClassification.REGULAR_VPA_DESCRIPTION);
  }

  @Test
  void shouldNormalizeCrossLinkClassificationCodes() {
    var realCaseProject = project(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE);
    var regularProject = project(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE);

    assertThat(new PublicStakeholderProjectDetailDTO(realCaseProject).getProjectClassification())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
    assertThat(new PublicStakeholderProjectDetailDTO(regularProject).getProjectClassification())
        .isEqualTo(ProjectClassification.REGULAR_VPA_DESCRIPTION);
  }

  @Test
  void shouldLeaveOtherClassificationDescriptionsUnaffected() {
    var project = project("Programme of Activities");

    var dto = new PublicStakeholderProjectDetailDTO(project);

    assertThat(dto.getProjectClassification()).isEqualTo("Programme of Activities");
  }

  private static ProjectBase project(String classification) {
    var project = new ProjectBase();
    project.setProjectClassification(classification);
    return project;
  }
}
