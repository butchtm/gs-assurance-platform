package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class CandidateParentEligibilityPolicyTest {

  private final CandidateParentEligibilityPolicy policy = new CandidateParentEligibilityPolicy();

  @Test
  void nativeRealCaseVpaRejectsCrossAccountPoa() {
    var poa = project(10, 2, ProjectClassification.programmeOfActivities(), null);

    var error =
        catchThrowable(
            () ->
                policy.validateParentEligibility(1, ProjectClassification.realCaseVpa(), poa, poa));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  @Test
  void crossLinkRealCaseVpaAllowsCrossAccountPoa() {
    var poa = project(10, 2, ProjectClassification.programmeOfActivities(), null);
    var classification =
        new ProjectClassification(ProjectClassification.REAL_CASE_VPA_CROSS_LINKAGE_ID);

    assertThatCode(() -> policy.validateParentEligibility(1, classification, poa, poa))
        .doesNotThrowAnyException();
  }

  @Test
  void crossLinkRegularVpaAllowsPoaCandidateDetail() {
    var poa = project(10, 2, ProjectClassification.programmeOfActivities(), null);
    var classification =
        new ProjectClassification(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);

    assertThatCode(() -> policy.validateCandidateParentDetails(1, classification, poa, null))
        .doesNotThrowAnyException();
  }

  @Test
  void crossLinkRegularVpaAllowsRealCaseCandidateDetailUnderSelectedPoa() {
    var poa = project(10, 2, ProjectClassification.programmeOfActivities(), null);
    var realCase = project(11, 2, ProjectClassification.realCaseVpa(), poa);
    var classification =
        new ProjectClassification(ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID);

    assertThatCode(() -> policy.validateCandidateParentDetails(1, classification, realCase, poa))
        .doesNotThrowAnyException();
  }

  @Test
  void regularVpaCandidateDetailRejectsCrossAccountPoa() {
    var poa = project(10, 2, ProjectClassification.programmeOfActivities(), null);

    var error =
        catchThrowable(
            () ->
                policy.validateCandidateParentDetails(
                    1, ProjectClassification.regularVpa(), poa, null));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  @Test
  void regularVpaAllowsRealCaseParentUnderSelectedPoa() {
    var poa = project(10, 1, ProjectClassification.programmeOfActivities(), null);
    var realCase = project(11, 1, ProjectClassification.realCaseVpa(), poa);

    assertThatCode(
            () ->
                policy.validateParentEligibility(
                    1, ProjectClassification.regularVpa(), poa, realCase))
        .doesNotThrowAnyException();
  }

  @Test
  void regularVpaRejectsRealCaseParentOutsideSelectedPoa() {
    var selectedPoa = project(10, 1, ProjectClassification.programmeOfActivities(), null);
    var otherPoa = project(12, 1, ProjectClassification.programmeOfActivities(), null);
    var realCase = project(11, 1, ProjectClassification.realCaseVpa(), otherPoa);

    var error =
        catchThrowable(
            () ->
                policy.validateParentEligibility(
                    1, ProjectClassification.regularVpa(), selectedPoa, realCase));

    assertThat(error).isInstanceOf(BadRequestException.class);
  }

  private static Project project(
      int id, int accountId, ProjectClassification classification, Project masterProject) {
    return Project.builder()
        .id(id)
        .accountId(accountId)
        .projectClassification(classification)
        .masterProject(masterProject)
        .build();
  }
}
