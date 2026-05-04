package org.goldstandard.assurance.api.entity.creditingperiod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.goldstandard.assurance.api.entity.lookup.Methodology;
import org.goldstandard.assurance.api.entity.lookup.ReferenceDataType;

@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Getter
@IdClass(ProjectCreditingPeriodMethodologyId.class)
public class ProjectCreditingPeriodMethodology {

  @Id
  @Column(name = "project_id")
  @With
  private final Integer projectId;

  @Id
  @Column(name = "period_id")
  private final Integer periodId;

  @Id
  @ManyToOne
  @JoinColumn(name = "methodology_id")
  private final Methodology methodology;

  @Column(nullable = false)
  private final Double version;

  public String getCode() {
    return getMethodologyField(ReferenceDataType::getCode);
  }

  public String getDescription() {
    return getMethodologyField(ReferenceDataType::getDescription);
  }

  public Integer getMethodologyId() {
    return getMethodologyField(ReferenceDataType::getId);
  }

  private <T> T getMethodologyField(Function<Methodology, T> function) {
    return Optional.ofNullable(methodology).map(function).orElse(null);
  }
}
