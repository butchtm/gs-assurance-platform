package org.goldstandard.assurance.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.goldstandard.assurance.api.entity.lookup.TransitionApprovalProcedure;
import org.goldstandard.assurance.api.entity.lookup.TransitionFromOtherScheme;
import org.goldstandard.assurance.api.entity.lookup.TransitionPathway;
import org.goldstandard.assurance.api.util.ProjectIdOnlySerializer;
import tools.jackson.databind.annotation.JsonSerialize;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TransitionProject {

  @Id
  @JsonIgnore
  @Column(name = "project_id")
  private Integer projectId;

  private String projectLinkInOtherScheme;
  private ZonedDateTime creditingPeriodStartDateOtherSchemes;
  private ZonedDateTime creditingPeriodEndDateOtherSchemes;
  @ManyToOne private TransitionApprovalProcedure transitionApprovalProcedure;
  @ManyToOne private TransitionFromOtherScheme transitionFromOtherScheme;
  @ManyToOne private TransitionPathway transitionPathway;

  @OneToOne
  @MapsId
  @JoinColumn(name = "project_id")
  @JsonSerialize(using = ProjectIdOnlySerializer.class)
  private Project project;

  public boolean hasTransitionDetails() {
    return StringUtils.isNotEmpty(projectLinkInOtherScheme)
        && Objects.nonNull(creditingPeriodStartDateOtherSchemes)
        && Objects.nonNull(creditingPeriodEndDateOtherSchemes)
        && Objects.nonNull(transitionApprovalProcedure)
        && hasScheme()
        && Objects.nonNull(transitionPathway);
  }

  private boolean hasScheme() {
    return Objects.nonNull(transitionFromOtherScheme) && !transitionFromOtherScheme.isNone();
  }
}
