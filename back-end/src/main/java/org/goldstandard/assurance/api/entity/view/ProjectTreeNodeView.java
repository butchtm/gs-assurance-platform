package org.goldstandard.assurance.api.entity.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.goldstandard.assurance.api.entity.lookup.Bundle;
import org.goldstandard.assurance.api.entity.lookup.ProjectClassification;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.goldstandard.assurance.api.entity.lookup.ProjectStatus;
import org.goldstandard.assurance.api.entity.lookup.ProjectType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity(name = "v_project_tree_node")
public class ProjectTreeNodeView {

  @Id private final Integer id;
  private final String gsid;
  private final Integer gsidNum;
  private final String name;
  private final Integer accountId;
  @ManyToOne private final ProjectClassification projectClassification;
  @ManyToOne private final Bundle bundle;
  @ManyToOne private final ProjectStatus projectStatus;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_type",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "project_type_id", referencedColumnName = "id"))
  private final List<ProjectType> projectTypes;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_location",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "project_location_id", referencedColumnName = "id"))
  private final List<ProjectLocation> projectLocations;

  @ManyToOne private final ProjectTreeNodeView masterProject;

  @ManyToOne private final ProjectTreeNodeView parentProject;

  public boolean isPoa() {
    return projectClassification.isPoa();
  }

  public boolean isRegularCaseVpa() {
    return projectClassification.isRegularCaseVpa();
  }

  public boolean isLegacyVpaCpa() {
    return projectClassification.isLegacyVpaCpa();
  }

  public boolean isRealCaseVpa() {
    return projectClassification.isRealCaseVpa();
  }

  public boolean hasParentProjectId(Integer otherId) {
    if (Objects.isNull(parentProject)) {
      return false;
    }
    return Objects.equals(parentProject.getId(), otherId);
  }
}
