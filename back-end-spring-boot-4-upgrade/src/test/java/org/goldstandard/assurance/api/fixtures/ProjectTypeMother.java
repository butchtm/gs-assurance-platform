package org.goldstandard.assurance.api.fixtures;

import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectTypeMother {

  public static List<ProjectType> withIds(Integer... ids) {
    return withIds(List.of(ids));
  }

  public static List<ProjectType> withIds(Collection<Integer> ids) {
    return ids.stream().map(ProjectTypeMother::withId).toList();
  }

  public static ProjectType withId(Integer id) {
    return builder().id(id).build();
  }

  public static ProjectType build() {
    return builder().build();
  }

  public static ProjectType.ProjectTypeBuilder<?, ?> builder() {
    return ProjectType.builder().id(0).code("BIOGAS_HEAT").description("Biogas - Heat");
  }
}
