package org.goldstandard.assurance.api.entity.view.stakeholder.project;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ProjectLocation;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Entity(name = "v_public_stakeholder_projects_basic")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBasicView {
  @Id private String gsid;
  private Integer projectId;
  private String projectName;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_project_location",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "project_location_id", referencedColumnName = "id"))
  private List<ProjectLocation> projectLocations;

  @Fetch(FetchMode.JOIN)
  @ManyToMany
  @JoinTable(
      name = "project_methodology",
      joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "projectId"),
      inverseJoinColumns = @JoinColumn(name = "methodology_id", referencedColumnName = "id"))
  private List<Methodology> projectMethodologies;
}
