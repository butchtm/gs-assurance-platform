package org.goldstandard.assurance.api.entity.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProjectClassificationTest {

  private static final int LEGACY_REAL_CASE_VPA_ID = 3;
  private static final int LEGACY_REGULAR_CASE_VPA_ID = 4;

  @Test
  void shouldTreatCrossLinkRealCaseVpaAsRealCaseOnly() {
    var classification =
        new ProjectClassification(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);

    assertThat(classification.isRealCaseVpa()).isTrue();
    assertThat(classification.isRegularCaseVpa()).isFalse();
  }

  @Test
  void shouldTreatCrossLinkRegularVpaAsRegularCaseOnly() {
    var classification =
        new ProjectClassification(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);

    assertThat(classification.isRegularCaseVpa()).isTrue();
    assertThat(classification.isRealCaseVpa()).isFalse();
  }

  @Test
  void shouldTreatNativeAndLegacyRealCaseIdsAsRealCaseVpa() {
    assertThat(new ProjectClassification(ProjectClassification.REAL_CASE_VPA_ID).isRealCaseVpa())
        .isTrue();
    assertThat(new ProjectClassification(LEGACY_REAL_CASE_VPA_ID).isRealCaseVpa()).isTrue();
    assertThat(new ProjectClassification(ProjectClassification.REGULAR_CASE_VPA_ID).isRealCaseVpa())
        .isFalse();
    assertThat(new ProjectClassification(LEGACY_REGULAR_CASE_VPA_ID).isRealCaseVpa()).isFalse();
  }

  @Test
  void shouldTreatNativeAndLegacyRegularIdsAsRegularCaseVpa() {
    assertThat(
            new ProjectClassification(ProjectClassification.REGULAR_CASE_VPA_ID).isRegularCaseVpa())
        .isTrue();
    assertThat(new ProjectClassification(LEGACY_REGULAR_CASE_VPA_ID).isRegularCaseVpa()).isTrue();
    assertThat(new ProjectClassification(ProjectClassification.REAL_CASE_VPA_ID).isRegularCaseVpa())
        .isFalse();
    assertThat(new ProjectClassification(LEGACY_REAL_CASE_VPA_ID).isRegularCaseVpa()).isFalse();
  }
}
