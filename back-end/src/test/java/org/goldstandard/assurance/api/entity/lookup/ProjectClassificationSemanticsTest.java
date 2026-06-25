package org.goldstandard.assurance.api.entity.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProjectClassificationSemanticsTest {

  @Test
  void shouldNormalizeRealCaseCrossLinkageClassificationToRealCaseDisplayClassification() {
    var classification =
        classification(
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_CODE,
            "Real Case VPA - Cross Linking",
            false);

    var displayClassification =
        ProjectClassificationSemantics.toDisplayClassification(classification);

    assertThat(displayClassification.getId()).isEqualTo(ProjectClassification.REAL_CASE_VPA_ID);
    assertThat(displayClassification.getCode()).isEqualTo(ProjectClassification.REAL_CASE_VPA_CODE);
    assertThat(displayClassification.getDescription())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
    assertThat(displayClassification.getActive()).isTrue();
  }

  @Test
  void shouldNormalizeRegularCrossLinkageClassificationToRegularDisplayClassification() {
    var classification =
        classification(
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE,
            "Regular VPA - Cross Linking",
            false);

    var displayClassification =
        ProjectClassificationSemantics.toDisplayClassification(classification);

    assertThat(displayClassification.getId()).isEqualTo(ProjectClassification.REGULAR_CASE_VPA_ID);
    assertThat(displayClassification.getCode()).isEqualTo(ProjectClassification.REGULAR_VPA_CODE);
    assertThat(displayClassification.getDescription())
        .isEqualTo(ProjectClassification.REGULAR_VPA_DESCRIPTION);
    assertThat(displayClassification.getActive()).isTrue();
  }

  @Test
  void shouldLeaveNativeClassificationDisplayValuesUnaffected() {
    var classification =
        classification(
            ProjectClassification.REAL_CASE_VPA_ID,
            ProjectClassification.REAL_CASE_VPA_CODE,
            ProjectClassification.REAL_CASE_VPA_DESCRIPTION,
            false);

    var displayClassification =
        ProjectClassificationSemantics.toDisplayClassification(classification);

    assertThat(displayClassification.getId()).isEqualTo(ProjectClassification.REAL_CASE_VPA_ID);
    assertThat(displayClassification.getCode()).isEqualTo(ProjectClassification.REAL_CASE_VPA_CODE);
    assertThat(displayClassification.getDescription())
        .isEqualTo(ProjectClassification.REAL_CASE_VPA_DESCRIPTION);
    assertThat(displayClassification.getActive()).isFalse();
  }

  @Test
  void shouldExpandDisplayFilterIdsToIncludeCrossLinkageIds() {
    var expandedIds =
        ProjectClassificationSemantics.expandDisplayFilterIds(
            List.of(
                ProjectClassification.REAL_CASE_VPA_ID, ProjectClassification.REGULAR_CASE_VPA_ID));

    assertThat(expandedIds)
        .containsExactlyInAnyOrder(
            ProjectClassification.REAL_CASE_VPA_ID,
            ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID,
            ProjectClassification.REGULAR_CASE_VPA_ID,
            ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);
  }

  @Test
  void shouldLeaveNativeFilterIdsUnaffectedWhenNoDisplayExpansionExists() {
    var expandedIds = ProjectClassificationSemantics.expandDisplayFilterIds(List.of(1, 2, 3));

    assertThat(expandedIds).containsExactlyInAnyOrder(1, 2, 3);
  }

  private static ProjectClassification classification(
      Integer id, String code, String description, Boolean active) {
    var classification = new ProjectClassification();
    classification.setId(id);
    classification.setCode(code);
    classification.setDescription(description);
    classification.setActive(active);
    return classification;
  }
}
