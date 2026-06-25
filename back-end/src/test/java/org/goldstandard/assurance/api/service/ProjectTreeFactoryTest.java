package org.goldstandard.assurance.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.view.ProjectTreeNodeView;
import org.junit.jupiter.api.Test;

class ProjectTreeFactoryTest {

  private final ProjectTreeFactory factory = new ProjectTreeFactory(null);

  @Test
  void shouldDisplayCrossLinkProjectClassificationAsCanonicalClassificationId() {
    var project =
        new ProjectTreeNodeView(
            1,
            "GS1",
            1,
            "Project 1",
            1,
            classification(
                ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_ID,
                ProjectClassification.REGULAR_VPA_CROSS_LINKAGE_CODE,
                "Regular VPA - Cross Linking"),
            null,
            null,
            List.of(),
            List.of(),
            null,
            null);

    var tree = factory.buildProjectTree(project);

    assertThat(tree.getRoot().getClassificationId())
        .isEqualTo(ProjectClassification.REGULAR_CASE_VPA_ID);
  }

  private static ProjectClassification classification(Integer id, String code, String description) {
    var classification = new ProjectClassification();
    classification.setId(id);
    classification.setCode(code);
    classification.setDescription(description);
    return classification;
  }
}
