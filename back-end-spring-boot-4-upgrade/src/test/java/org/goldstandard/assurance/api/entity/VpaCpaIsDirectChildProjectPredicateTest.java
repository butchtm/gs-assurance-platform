package org.goldstandard.assurance.api.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VpaCpaIsDirectChildProjectPredicateTest {

  private static final Project POA =
      Project.builder()
          .id(1)
          .projectClassification(ProjectClassification.programmeOfActivities())
          .build();
  private static final Project VPA_CPA_1 =
      Project.builder()
          .id(2)
          .masterProject(POA)
          .parentProject(POA)
          .projectClassification(ProjectClassification.legacyVpaCpa())
          .build();
  private static final Project VPA_CPA_2 =
      Project.builder()
          .id(3)
          .masterProject(POA)
          .parentProject(POA)
          .projectClassification(ProjectClassification.legacyVpaCpa())
          .build();

  @ParameterizedTest
  @MethodSource("parentProjectAndExpectedChildProjects")
  void shouldReturnChildProjectsForParentProject(
      Project parentProject, Collection<Project> expectedChildProjects) {
    var predicate = new VpaCpaIsDirectChildProjectPredicate(parentProject);

    var childProjects = Stream.of(VPA_CPA_1, VPA_CPA_2).filter(predicate).toList();

    assertThat(childProjects).containsExactlyElementsOf(expectedChildProjects);
  }

  private static Stream<Arguments> parentProjectAndExpectedChildProjects() {
    return Stream.of(Arguments.of(VPA_CPA_1, List.of(VPA_CPA_2)));
  }
}
