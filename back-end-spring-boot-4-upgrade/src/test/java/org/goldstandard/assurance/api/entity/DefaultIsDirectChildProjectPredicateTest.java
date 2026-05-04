package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultIsDirectChildProjectPredicateTest {

  private static final Project POA =
      Project.builder()
          .id(1)
          .projectClassification(ProjectClassification.programmeOfActivities())
          .build();
  private static final Project REAL_CASE =
      Project.builder()
          .id(2)
          .parentProject(POA)
          .masterProject(POA)
          .projectClassification(ProjectClassification.realCaseVpa())
          .build();
  private static final Project REGULAR_CASE_1 =
      Project.builder()
          .id(3)
          .masterProject(POA)
          .parentProject(REAL_CASE)
          .projectClassification(ProjectClassification.regularVpa())
          .build();
  private static final Project REGULAR_CASE_2 =
      Project.builder()
          .id(4)
          .masterProject(POA)
          .parentProject(REAL_CASE)
          .projectClassification(ProjectClassification.regularVpa())
          .build();
  private static final Project VPA_CPA =
      Project.builder()
          .id(5)
          .masterProject(POA)
          .parentProject(POA)
          .projectClassification(ProjectClassification.legacyVpaCpa())
          .build();

  @ParameterizedTest
  @MethodSource("parentProjectAndExpectedChildProjects")
  void shouldReturnChildProjectsForParentProject(
      Project parentProject, Collection<Project> expectedChildProjects) {
    var predicate = new DefaultIsDirectChildProjectPredicate(parentProject);

    var childProjects =
        Stream.of(POA, REAL_CASE, REGULAR_CASE_1, REGULAR_CASE_2, VPA_CPA)
            .filter(predicate)
            .toList();

    assertThat(childProjects).containsExactlyElementsOf(expectedChildProjects);
  }

  private static Stream<Arguments> parentProjectAndExpectedChildProjects() {
    return Stream.of(
        Arguments.of(POA, List.of(REAL_CASE)),
        Arguments.of(REAL_CASE, List.of(REGULAR_CASE_1, REGULAR_CASE_2)),
        Arguments.of(REGULAR_CASE_1, List.of(REGULAR_CASE_2)));
  }
}
