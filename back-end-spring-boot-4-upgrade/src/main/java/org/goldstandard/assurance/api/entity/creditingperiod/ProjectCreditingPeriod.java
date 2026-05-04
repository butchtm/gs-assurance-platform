package org.goldstandard.assurance.api.entity.creditingperiod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.Project;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Entity
@IdClass(ProjectCreditingPeriodId.class)
public class ProjectCreditingPeriod {

  @Id private final Integer projectId;
  @Id private final Integer periodId;

  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private final Project project;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "project_id",
      insertable = false,
      updatable = false)
  @JoinColumn(
      name = "period_id",
      referencedColumnName = "period_id",
      insertable = false,
      updatable = false)
  @With
  private final List<ProjectCreditingPeriodMethodology> methodologies;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "project_id",
      insertable = false,
      updatable = false)
  @JoinColumn(
      name = "period_id",
      referencedColumnName = "period_id",
      insertable = false,
      updatable = false)
  @With
  private final Collection<ProjectCreditingMonitoringPeriod> monitoringPeriods;

  public ProjectCreditingPeriod withProject(Project project) {
    return toBuilder()
        .projectId(project.getId())
        .project(project)
        .methodologies(toMethodologiesWithProjectId(project.getId()))
        .monitoringPeriods(toMonitoringPeriodsWithProjectId(project.getId()))
        .build();
  }

  private List<ProjectCreditingMonitoringPeriod> toMonitoringPeriodsWithProjectId(
      Integer projectId) {
    return Optional.ofNullable(monitoringPeriods).stream()
        .flatMap(Collection::stream)
        .map(mp -> mp.withProjectId(projectId))
        .toList();
  }

  private List<ProjectCreditingPeriodMethodology> toMethodologiesWithProjectId(Integer projectId) {
    return Optional.ofNullable(methodologies).stream()
        .flatMap(Collection::stream)
        .map(m -> m.withProjectId(projectId))
        .toList();
  }
}
