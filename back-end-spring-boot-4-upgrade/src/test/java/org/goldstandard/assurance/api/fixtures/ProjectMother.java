package org.goldstandard.assurance.api.fixtures;

import static org.goldstandard.assurance.api.populator.AccountPopulator.PROJECT_DEVELOPER_ACCOUNT_ID;

import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.Project;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectMother {

  public static Project newProjectWithTypes(Integer... typeIds) {
    return newProjectWithTypes(List.of(typeIds));
  }

  public static Project newProjectWithTypes(List<Integer> typeIds) {
    return ProjectMother.builder()
        .id(null)
        .projectTypes(ProjectTypeMother.withIds(typeIds))
        .build();
  }

  public static Project withIdOnly(Integer id) {
    return ProjectMother.builder().id(id).build();
  }

  public static Project withNameAndGsid(String name, String gsid) {
    return builder()
        .name(name)
        .gsid(gsid)
        .projectTypes(java.util.Collections.emptyList())
        .projectLocations(java.util.Collections.emptyList())
        .build();
  }

  public static Project build() {
    return builder().build();
  }

  public static Project.ProjectBuilder builder() {
    return Project.builder()
        .id(1)
        .projectClassification(ProjectClassification.programmeOfActivities())
        .projectStatus(ProjectStatus.newStatus())
        .methodologies(Collections.emptyList())
        .projectAssets(Collections.emptyList())
        .projectLocations(Collections.emptyList())
        .projectTypes(Collections.emptyList())
        .sustainableDevelopmentGoals(Collections.emptyList())
        .accountId(PROJECT_DEVELOPER_ACCOUNT_ID);
  }
}
